package com.example.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object TelegramNotifier {
    // توکن ربات تلگرام اختصاصی HM Hammer
    private const val BOT_TOKEN = "7738209824:AAGs_XoHj0F1lZp6V8q_hmHammerBotKeyMock"
    private const val ADMIN_CHAT_ID = "589210482" // آی‌دی تلگرام مدیریت جهت دریافت ریز گزارش‌ها

    suspend fun sendNotification(message: String, userChatId: String? = null) {
        withContext(Dispatchers.IO) {
            try {
                // ارسال به ادمین
                postMessage(ADMIN_CHAT_ID, message)
                // ارسال به کاربر در صورت داشتن شناسه تلگرام
                if (!userChatId.isNullOrBlank() && userChatId != ADMIN_CHAT_ID) {
                    postMessage(userChatId, message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun postMessage(chatId: String, text: String) {
        try {
            val encodedText = URLEncoder.encode(text, "UTF-8")
            val urlString = "https://api.telegram.org/bot$BOT_TOKEN/sendMessage?chat_id=$chatId&text=$encodedText&parse_mode=Markdown"
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
