package com.example.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object WallexLiveClient {
    private const val BASE_URL = "https://api.wallex.ir/v1"

    suspend fun placeOrder(
        apiKey: String,
        symbol: String,
        type: String, // "buy" or "sell"
        quantity: Double,
        price: Double
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL/orders")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("X-API-Key", apiKey)
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 10000
            conn.readTimeout = 10000

            val payload = JSONObject().apply {
                put("symbol", symbol)
                put("type", type.lowercase())
                put("order_type", "limit")
                put("quantity", quantity)
                put("price", price)
            }

            OutputStreamWriter(conn.outputStream).use { it.write(payload.toString()) }

            val responseCode = conn.responseCode
            val stream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
            val responseText = stream.bufferedReader().readText()

            if (responseCode in 200..299) {
                val json = JSONObject(responseText)
                val orderId = json.optJSONObject("result")?.optString("client_order_id") ?: "SUCCESS"
                Result.success(orderId)
            } else {
                Result.failure(Exception("خطای صرافی ($responseCode): $responseText"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("خطای شبکه یا عدم دسترسی به صرافی: ${e.message}"))
        }
    }
}
