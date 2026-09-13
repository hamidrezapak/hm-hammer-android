package com.example.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object TelegramNotifier {

    suspend fun sendNotification(message: String, userChatId: String? = null) {
        withContext(Dispatchers.IO) {
            val botToken = TelegramConfigStore.getBotToken()
            val adminChatId = TelegramConfigStore.getAdminChatId()
            if (botToken.isBlank() || adminChatId.isBlank()) return@withContext

            try {
                postMessage(botToken, adminChatId, message)
                if (!userChatId.isNullOrBlank() && userChatId != adminChatId) {
                    postMessage(botToken, userChatId, message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun postMessage(botToken: String, chatId: String, text: String) {
        try {
            val encodedText = URLEncoder.encode(text, "UTF-8")
            val urlString = "https://api.telegram.org/bot$botToken/sendMessage?chat_id=$chatId&text=$encodedText&parse_mode=Markdown"
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 4000
            conn.readTimeout = 4000
            conn.responseCode
            conn.disconnect()
        } catch (_: Exception) {}
    }
}
