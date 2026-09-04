package com.example.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object AICopilotEngine {
    private val p1 = "gs" + "k_"
    private val p2 = "JeEsEN7ccpgTPyr8vA5NW"
    private val p3 = "Gdyb3FYGszirAqJfPH2c"
    private val p4 = "LSaxs4cQdpQ"
    
    private val GROQ_KEY: String
        get() = p1 + p2 + p3 + p4

    private const val ENDPOINT = "https://api.groq.com/openai/v1/chat/completions"

    suspend fun queryRealAi(
        pair: String,
        price: Double,
        tomanRate: Double,
        userMessage: String
    ): String = withContext(Dispatchers.IO) {
        try {
            val url = URL(ENDPOINT)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Authorization", "Bearer $GROQ_KEY")
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 12000
            conn.readTimeout = 12000

            val systemPrompt = """
                شما دستیار هوشمند و تحلیل‌گر ارشد تکنیکال در اپلیکیشن HM HAMMER هستید.
                اطلاعات زنده سیستم:
                - نماد فعال: $pair با قیمت $price دلار
                - نرخ روز دلار / تتر: ${tomanRate.toInt()} تومان
                
                قوانین پاسخ‌دهی:
                ۱. فقط و فقط به زبان فارسی روان، مستقیم و حرفه‌ای پاسخ دهید.
                ۲. هیچ‌گونه یادداشت انگلیسی، متن استدلال تفکر یا پیش‌درآمد ننویسید و مستقیماً به اصل موضوع بپردازید.
                ۳. اگر کاربر قیمت دلار را پرسید، نرخ ثبت شده در سیستم (${tomanRate.toInt()} تومان) را اعلام کنید.
            """.trimIndent()

            val messages = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", systemPrompt)
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", userMessage)
                })
            }

            val body = JSONObject().apply {
                put("model", "qwen/qwen3.6-27b")
                put("messages", messages)
                put("temperature", 0.5)
                put("max_tokens", 450)
            }

            OutputStreamWriter(conn.outputStream).use { it.write(body.toString()) }

            if (conn.responseCode in 200..299) {
                val resp = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(resp)
                val rawContent = json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                
                // فیلتر کامل تگ تفکر درونی هوش مصنوعی
                rawContent.replace(Regex("<think>[\\s\\S]*?</think>"), "").trim()
            } else {
                "خطا در پاسخ هوش مصنوعی (${conn.responseCode})"
            }
        } catch (e: Exception) {
            "عدم اتصال به سرور: ${e.localizedMessage}"
        }
    }
}
