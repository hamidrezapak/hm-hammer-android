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

object WallexLiveClient {
    private const val BASE_URL = "https://api.wallex.ir/v1"

    fun getStepSize(symbol: String): Int {
        val upper = symbol.uppercase(Locale.ROOT)
        return when {
            upper.startsWith("BTC") -> 6
            upper.startsWith("ETH") -> 5
            upper.startsWith("SOL") -> 3
            upper.startsWith("DOGE") || upper.startsWith("TRX") -> 1
            else -> 4
        }
    }

    fun getPricePrecision(symbol: String): Int {
        val upper = symbol.uppercase(Locale.ROOT)
        return if (upper.endsWith("TMN")) 0 else 2
    }

    fun formatQuantity(symbol: String, qty: Double): Double {
        val decimals = getStepSize(symbol)
        val factor = Math.pow(10.0, decimals.toDouble())
        return Math.floor(qty * factor) / factor
    }

    fun formatPrice(symbol: String, price: Double): Double {
        val decimals = getPricePrecision(symbol)
        val factor = Math.pow(10.0, decimals.toDouble())
        return Math.round(price * factor) / factor
    }

    suspend fun fetchMarketPrice(symbol: String): Result<Double> = withContext(Dispatchers.IO) {
        var conn: HttpURLConnection? = null
        try {
            val url = URL("$BASE_URL/markets")
            conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 7000
                readTimeout = 7000
            }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val res = stream?.bufferedReader()?.readText().orEmpty()

            if (code in 200..299) {
                val json = JSONObject(res)
                val markets = json.getJSONObject("result").getJSONObject("symbols")
                val stats = markets.getJSONObject(symbol).getJSONObject("stats")
                Result.success(stats.getDouble("lastPrice"))
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
                    val value = assetObj?.optDouble("value", 0.0) ?: 0.0
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
            val cleanQty = formatQuantity(symbol, quantity)
            val cleanPrice = formatPrice(symbol, price)

            if (cleanQty * cleanPrice < 1.0) {
                return@withContext Result.failure(WallexException.OrderRejectedException("Total value below minimum (1 USDT)"))
            }

            val url = URL("$BASE_URL/orders")
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("X-API-Key", apiKey.trim())
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
}
