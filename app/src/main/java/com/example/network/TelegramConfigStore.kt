package com.example.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object TelegramConfigStore {
    private const val PREFS_NAME = "telegram_secure_vault"
    private const val KEY_BOT_TOKEN = "bot_token"
    private const val KEY_ADMIN_CHAT_ID = "admin_chat_id"
    private const val TAG = "TelegramConfigStore"

    @Volatile private var memoryBotToken: String = ""
    @Volatile private var memoryAdminChatId: String = ""
    @Volatile private var appContext: Context? = null

    fun init(context: Context) {
        if (appContext == null) appContext = context.applicationContext
        getBotToken()
        getAdminChatId()
    }

    private fun getPrefs(): SharedPreferences? {
        val ctx = appContext ?: run {
            Log.e(TAG, "TelegramConfigStore.init(context) هنوز فراخوانی نشده است")
            return null
        }
        return try {
            val masterKey = MasterKey.Builder(ctx)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                ctx, PREFS_NAME, masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e(TAG, "خطا در ساخت EncryptedSharedPreferences: ${e.message}")
            null
        }
    }

    fun saveConfig(botToken: String, adminChatId: String) {
        memoryBotToken = botToken.trim()
        memoryAdminChatId = adminChatId.trim()
        try {
            getPrefs()?.edit()
                ?.putString(KEY_BOT_TOKEN, memoryBotToken)
                ?.putString(KEY_ADMIN_CHAT_ID, memoryAdminChatId)
                ?.apply()
        } catch (e: Exception) {
            Log.e(TAG, "خطا در ذخیره تنظیمات تلگرام: ${e.message}")
        }
    }

    fun getBotToken(): String {
        if (memoryBotToken.isBlank()) {
            try {
                memoryBotToken = getPrefs()?.getString(KEY_BOT_TOKEN, "") ?: ""
            } catch (e: Exception) {
                Log.e(TAG, "خطا در خواندن توکن ربات: ${e.message}")
            }
        }
        return memoryBotToken
    }

    fun getAdminChatId(): String {
        if (memoryAdminChatId.isBlank()) {
            try {
                memoryAdminChatId = getPrefs()?.getString(KEY_ADMIN_CHAT_ID, "") ?: ""
            } catch (e: Exception) {
                Log.e(TAG, "خطا در خواندن chat id: ${e.message}")
            }
        }
        return memoryAdminChatId
    }

    fun isConfigured(): Boolean = getBotToken().isNotBlank() && getAdminChatId().isNotBlank()
}
