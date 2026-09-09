package com.example.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

sealed class WallexException(message: String) : Exception(message) {
    class AuthException(msg: String) : WallexException("Authentication Failed (401/403): $msg")
    class NetworkException(msg: String) : WallexException("Network Error: $msg")
    class OrderRejectedException(msg: String) : WallexException("Order Rejected: $msg")
}

data class SymbolInfo(
    val stepSize: Int,
    val pricePrecision: Int,
    val minQty: Double,
    val minNotional: Double
)

object WallexLiveClient {
    private const val BASE_URL = "https://api.wallex.ir/v1"
    private const val USER_AGENT = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"

    private val symbolInfoCache = mutableMapOf<String, SymbolInfo>()

    private fun fallbackStepSize(symbol: String): Int {
        val upper = symbol.uppercase(Locale.ROOT)
        return when {
            upper.startsWith("BTC") -> 6
            upper.startsWith("ETH") -> 5
            upper.startsWith("SOL") -> 3
            upper.startsWith("DOGE") || upper.startsWith("TRX") -> 1
            else -> 4
        }
    }

    private fun fallbackPricePrecision(symbol: String): Int {
        val upper = symbol.uppercase(Locale.ROOT)
        return if (upper.endsWith("TMN")) 0 else 2
    }

    private fun fallbackMinNotional(symbol: String): Double {
        val upper = symbol.uppercase(Locale.ROOT)
        return when {
            upper.endsWith("TMN") -> 200_000.0
            upper.endsWith("USDT") -> 1.0
            else -> 1.0
        }
    }

    fun getStepSize(symbol: String): Int = fallbackStepSize(symbol)
    fun getPricePrecision(symbol: String): Int = fallbackPricePrecision(symbol)

    fun formatQuantity(symbol: String, qty: Double): Double {
        val factor = Math.pow(10.0, getStepSize(symbol).toDouble())
        return Math.floor(qty * factor) / factor
    }

    fun formatPrice(symbol: String, price: Double): Double {
        val factor = Math.pow(10.0, getPricePrecision(symbol).toDouble())
        return Math.round(price * factor) / factor
    }

    suspend fun fetchSymbolInfo(symbol: String): SymbolInfo = withContext(Dispatchers.IO) {
        val upper = symbol.uppercase(Locale.ROOT)
        symbolInfoCache[upper]?.let { return@withContext it }

        var conn: HttpURLConnection? = null
        try {
            val url = URL("$BASE_URL/markets")
            conn = (url.openConnection() as HttpURLConnection).apply {
                setRequestProperty("User-Agent", USER_AGENT)
                connectTimeout = 7000
                readTimeout = 7000
            }
            val res = conn.inputStream.bufferedReader().readText()
            val json = JSONObject(res)
            val symbolData = json.getJSONObject("result").getJSONObject("symbols").getJSONObject(upper)

            val stepSize = symbolData.optInt("baseAssetPrecision", fallbackStepSize(upper))
            val pricePrecision = symbolData.optInt("quoteAssetPrecision", fallbackPricePrecision(upper))
            val minQty = symbolData.optDouble("minQty", 0.0)
            val minNotional = symbolData.optDouble("minNotional", fallbackMinNotional(upper))

            val info = SymbolInfo(stepSize, pricePrecision, minQty, minNotional)
            symbolInfoCache[upper] = info
            info
        } catch (e: Exception) {
            SymbolInfo(
                stepSize = fallbackStepSize(upper),
                pricePrecision = fallbackPricePrecision(upper),
                minQty = 0.0,
                minNotional = fallbackMinNotional(upper)
            )
        } finally {
            conn?.disconnect()
        }
    }

    suspend fun fetchMarketPrice(symbol: String): Result<Double> = withContext(Dispatchers.IO) {
        var conn: HttpURLConnection? = null
        try {
            val url = URL("$BASE_URL/markets")
            conn = (url.openConnection() as HttpURLConnection).apply {
                setRequestProperty("User-Agent", USER_AGENT)
                connectTimeout = 7000
                readTimeout = 7000
            }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val res = stream?.bufferedReader()?.readText().orEmpty()

            if (code in 200..299) {
                val json = JSONObject(res)
                val markets = json.getJSONObject("result").getJSONObject("symbols")
                val stats = markets.getJSONObject(symbol.uppercase(Locale.ROOT)).getJSONObject("stats")
                val price = stats.optDouble("lastPrice", stats.optString("lastPrice", "0.0").toDoubleOrNull() ?: 0.0)
                Result.success(price)
            } else {
                Result.failure(WallexException.NetworkException("HTTP $code: $res"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            conn?.disconnect()
        }
    }

    suspend fun fetchBalance(apiKey: String, asset: String): Result<Double> = withContext(Dispatchers.IO) {
        var conn: HttpURLConnection? = null
        try {
            val url = URL("$BASE_URL/account/balances")
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("X-API-Key", apiKey.trim())
                setRequestProperty("User-Agent", USER_AGENT)
                setRequestProperty("Content-Type", "application/json")
                connectTimeout = 7000
                readTimeout = 7000
            }

            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val res = stream?.bufferedReader()?.readText().orEmpty()

            when (code) {
                in 200..299 -> {
                    val json = JSONObject(res)
                    val balances = json.getJSONObject("result").getJSONObject("balances")
                    val assetObj = balances.optJSONObject(asset.uppercase(Locale.ROOT))
                    val rawVal = assetObj?.opt("value")?.toString() ?: "0.0"
                    val value = rawVal.toDoubleOrNull() ?: 0.0
                    Result.success(value)
                }
                401, 403 -> Result.failure(WallexException.AuthException(res))
                else -> Result.failure(WallexException.NetworkException("HTTP $code: $res"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            conn?.disconnect()
        }
    }

    suspend fun executeOrder(
        apiKey: String,
        symbol: String,
        side: String,
        quantity: Double,
        price: Double
    ): Result<String> = withContext(Dispatchers.IO) {
        var conn: HttpURLConnection? = null
        try {
            val info = fetchSymbolInfo(symbol)
            val cleanQty = formatQuantity(symbol, quantity)
            val cleanPrice = formatPrice(symbol, price)

            if (cleanQty * cleanPrice < info.minNotional) {
                return@withContext Result.failure(
                    WallexException.OrderRejectedException(
                        "ارزش سفارش (${cleanQty * cleanPrice}) کمتر از حداقل مجاز (${info.minNotional}) است"
                    )
                )
            }
            if (info.minQty > 0.0 && cleanQty < info.minQty) {
                return@withContext Result.failure(
                    WallexException.OrderRejectedException("حجم سفارش کمتر از حداقل مجاز (${info.minQty}) است")
                )
            }

            val url = URL("$BASE_URL/orders")
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("X-API-Key", apiKey.trim())
                setRequestProperty("User-Agent", USER_AGENT)
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
                connectTimeout = 8000
                readTimeout = 8000
            }

            val payload = JSONObject().apply {
                put("symbol", symbol.uppercase(Locale.ROOT))
                put("type", "LIMIT")
                put("side", side.uppercase(Locale.ROOT))
                put("price", String.format(Locale.US, "%.${getPricePrecision(symbol)}f", cleanPrice))
                put("quantity", String.format(Locale.US, "%.${getStepSize(symbol)}f", cleanQty))
            }

            OutputStreamWriter(conn.outputStream).use { it.write(payload.toString()) }

            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val res = stream?.bufferedReader()?.readText().orEmpty()

            if (code in 200..299) {
                val json = JSONObject(res)
                val result = json.getJSONObject("result")
                val orderId = result.optString("order_id", result.optString("id", "OK"))
                Result.success(orderId)
            } else {
                Result.failure(WallexException.OrderRejectedException("HTTP $code: $res"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            conn?.disconnect()
        }
    }

    suspend fun checkOrderStatus(apiKey: String, orderId: String): Result<String> = withContext(Dispatchers.IO) {
        var conn: HttpURLConnection? = null
        try {
            val url = URL("$BASE_URL/orders/$orderId")
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("X-API-Key", apiKey.trim())
                setRequestProperty("User-Agent", USER_AGENT)
                setRequestProperty("Content-Type", "application/json")
                connectTimeout = 7000
                readTimeout = 7000
            }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val res = stream?.bufferedReader()?.readText().orEmpty()

            if (code in 200..299) {
                val json = JSONObject(res)
                val status = json.optJSONObject("result")?.optString("status", "UNKNOWN") ?: "UNKNOWN"
                Result.success(status.uppercase(Locale.ROOT))
            } else {
                Result.failure(WallexException.NetworkException("HTTP $code: $res"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            conn?.disconnect()
        }
    }

    suspend fun cancelOrder(apiKey: String, orderId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        var conn: HttpURLConnection? = null
        try {
            val url = URL("$BASE_URL/orders/$orderId")
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "DELETE"
                setRequestProperty("X-API-Key", apiKey.trim())
                setRequestProperty("User-Agent", USER_AGENT)
                setRequestProperty("Content-Type", "application/json")
                connectTimeout = 7000
                readTimeout = 7000
            }
            val code = conn.responseCode
            Result.success(code in 200..299)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            conn?.disconnect()
        }
    }

    suspend fun placeOrder(
        apiKey: String,
        symbol: String,
        type: String,
        quantity: Double,
        price: Double
    ): Result<String> = executeOrder(
        apiKey = apiKey,
        symbol = symbol,
        side = type,
        quantity = quantity,
        price = price
    )
}
