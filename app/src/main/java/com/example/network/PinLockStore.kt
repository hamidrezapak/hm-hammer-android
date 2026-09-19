package com.example.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest

object PinLockStore {
    private const val PREFS_NAME = "pin_lock_secure_vault"
    private const val KEY_PIN_HASH = "pin_hash"
    private const val TAG = "PinLockStore"

    @Volatile private var appContext: Context? = null

    fun init(context: Context) {
        if (appContext == null) appContext = context.applicationContext
    }

    private fun getPrefs(): SharedPreferences? {
        val ctx = appContext ?: return null
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

    private fun hash(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun hasPinSet(): Boolean = try {
        !getPrefs()?.getString(KEY_PIN_HASH, null).isNullOrBlank()
    } catch (e: Exception) { false }

    fun savePin(pin: String) {
        try {
            getPrefs()?.edit()?.putString(KEY_PIN_HASH, hash(pin))?.apply()
        } catch (e: Exception) {
            Log.e(TAG, "خطا در ذخیره PIN: ${e.message}")
        }
    }

    fun verifyPin(pin: String): Boolean = try {
        val stored = getPrefs()?.getString(KEY_PIN_HASH, null)
        stored != null && stored == hash(pin)
    } catch (e: Exception) { false }

    fun clearPin() {
        try {
            getPrefs()?.edit()?.remove(KEY_PIN_HASH)?.apply()
        } catch (e: Exception) {
            Log.e(TAG, "خطا در حذف PIN: ${e.message}")
        }
    }
}
