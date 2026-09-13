package com.example.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SubscriptionStore {
    private const val PREFS_NAME = "subscription_secure_vault"
    private const val KEY_ACTIVE_PLAN = "active_plan_id"
    private const val KEY_ACTIVE_EXPIRY = "active_expiry_millis"
    private const val KEY_PENDING_PLAN = "pending_plan_id"
    private const val KEY_PENDING_TRACKING = "pending_tracking_code"
    private const val TAG = "SubscriptionStore"

    @Volatile private var appContext: Context? = null

    fun init(context: Context) {
        if (appContext == null) appContext = context.applicationContext
    }

    private fun getPrefs(): SharedPreferences? {
        val ctx = appContext ?: run {
            Log.e(TAG, "SubscriptionStore.init(context) هنوز فراخوانی نشده است")
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

    fun savePendingClaim(planId: String, trackingCode: String) {
        try {
            getPrefs()?.edit()
                ?.putString(KEY_PENDING_PLAN, planId)
                ?.putString(KEY_PENDING_TRACKING, trackingCode.trim())
                ?.apply()
        } catch (e: Exception) {
            Log.e(TAG, "خطا در ذخیره درخواست: ${e.message}")
        }
    }

    fun getPendingPlan(): String = try { getPrefs()?.getString(KEY_PENDING_PLAN, "") ?: "" } catch (e: Exception) { "" }
    fun getPendingTracking(): String = try { getPrefs()?.getString(KEY_PENDING_TRACKING, "") ?: "" } catch (e: Exception) { "" }

    fun activatePlan(planId: String, expiryMillis: Long) {
        try {
            getPrefs()?.edit()
                ?.putString(KEY_ACTIVE_PLAN, planId)
                ?.putLong(KEY_ACTIVE_EXPIRY, expiryMillis)
                ?.remove(KEY_PENDING_PLAN)
                ?.remove(KEY_PENDING_TRACKING)
                ?.apply()
        } catch (e: Exception) {
            Log.e(TAG, "خطا در فعال‌سازی پلن: ${e.message}")
        }
    }

    fun getActivePlan(): String? {
        val plan = try { getPrefs()?.getString(KEY_ACTIVE_PLAN, null) } catch (e: Exception) { null }
        val expiry = try { getPrefs()?.getLong(KEY_ACTIVE_EXPIRY, 0L) ?: 0L } catch (e: Exception) { 0L }
        return if (plan != null && expiry > System.currentTimeMillis()) plan else null
    }

    fun getActiveExpiry(): Long = try { getPrefs()?.getLong(KEY_ACTIVE_EXPIRY, 0L) ?: 0L } catch (e: Exception) { 0L }
}
