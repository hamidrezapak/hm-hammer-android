package com.example.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecureKeyStore {

    private const val PREFS_NAME = "wallex_secure_vault"
    private const val KEY_API = "api_key"
    private const val TAG = "SecureKeyStore"

    @Volatile
    private var memoryKey: String = ""

    @Volatile
    private var appContext: Context? = null

    fun init(context: Context) {
        if (appContext == null) {
            appContext = context.applicationContext
        }
        getKey()
    }

    private fun getPrefs(): SharedPreferences? {
        val ctx = appContext ?: run {
            Log.e(TAG, "SecureKeyStore.init(context) هنوز فراخوانی نشده است")
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

    fun saveKey(key: String) {
        val clean = key.trim()
        memoryKey = clean
        try {
            getPrefs()?.edit()?.putString(KEY_API, clean)?.apply()
        } catch (e: Exception) {
            Log.e(TAG, "خطا در ذخیره کلید: ${e.message}")
        }
    }

    fun getKey(): String {
        if (memoryKey.isBlank()) {
            try {
                memoryKey = getPrefs()?.getString(KEY_API, "") ?: ""
            } catch (e: Exception) {
                Log.e(TAG, "خطا در خواندن کلید: ${e.message}")
            }
        }
        return memoryKey
    }

    fun hasKey(): Boolean = getKey().isNotBlank()

    fun clearKey() {
        memoryKey = ""
        try {
            getPrefs()?.edit()?.remove(KEY_API)?.apply()
        } catch (e: Exception) {
            Log.e(TAG, "خطا در پاک‌سازی کلید: ${e.message}")
        }
    }
}
