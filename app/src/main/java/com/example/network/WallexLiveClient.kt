package com.example.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

object WallexLiveClient {
    private const val BASE_URL = "https://api.wallex.ir/v1"

    fun formatQuantity(symbol: String, qty: Double): Double {
        val decimals = when {
            symbol.startsWith("BTC") -> 5
            symbol.startsWith("ETH") -> 4
            symbol.startsWith("SOL") -> 3
            symbol.startsWith("DOGE") || symbol.startsWith("TRX") -> 1
            else -> 4
        }
        val factor = Math.pow(10.0, decimals.toDouble())
        return Math.floor(qty * factor) / factor
    }

    fun formatPrice(symbol: String, price: Double): Double {
        val decimals = if (symbol.endsWith("TMN")) 0 else 2
        val factor = Math.pow(10.0, decimals.toDouble())
        return Math.round(price * factor) / factor
    }

    suspend fun placeOrder(
        apiKey: String,
        symbol: String,
        type: String,
        quantity: Double,
        price: Double
    ): Result<String> = withContext(Dispatchers.IO) {
        val cleanQty = formatQuantity(symbol, quantity)
        val slippage = if (type.equals("buy", ignoreCase = true)) 1.0015 else 0.9985
        val aggressivePrice = formatPrice(symbol, price * slippage)

        if (cleanQty * aggressivePrice < 2.0) {
            return@withContext Result.failure(Exception("ارزش کل سفارش کمتر از حداقل مجاز صرافی (2 USDT) است."))
        }

        var lastException: Exception? = null
        for (attempt in 1..3) {
            try {
                val url = URL("$BASE_URL/orders")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("X-API-Key", apiKey)
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true
                conn.connectTimeout = 8000
                conn.readTimeout = 8000

                val payload = JSONObject().apply {
                    put("symbol", symbol)
                    put("type", type.lowercase(Locale.ROOT))
                    put("order_type", "limit")
                    put("quantity", cleanQty)
                    put("price", aggressivePrice)
                }

                OutputStreamWriter(conn.outputStream).use { it.write(payload.toString()) }

                val responseCode = conn.responseCode
                val stream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
                val responseText = stream.bufferedReader().readText()

                if (responseCode in 200..299) {
                    val json = JSONObject(responseText)
                    val orderId = json.optJSONObject("result")?.optString("client_order_id") ?: "EXECUTED_OK"
                    return@withContext Result.success(orderId)
                } else {
                    lastException = Exception("خطای صرافی ($responseCode): $responseText")
                }
            } catch (e: Exception) {
                lastException = e
            }
            if (attempt < 3) delay(1000)
        }
        Result.failure(lastException ?: Exception("عدم اتصال به صرافی پس از ۳ تلاش مجدد."))
    }
}
