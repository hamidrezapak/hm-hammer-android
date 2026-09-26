package com.example.network

import android.content.Context
import android.content.SharedPreferences

object TradePrefsStore {
    private const val PREFS_NAME = "hm_hammer_trade_prefs"
    private const val KEY_ALLOCATION = "selected_allocation"
    private const val KEY_LEVERAGE = "selected_leverage"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getAllocation(): Int =
        if (::prefs.isInitialized) prefs.getInt(KEY_ALLOCATION, 25) else 25

    fun saveAllocation(percent: Int) {
        if (::prefs.isInitialized) prefs.edit().putInt(KEY_ALLOCATION, percent).apply()
    }

    fun getLeverage(): String =
        if (::prefs.isInitialized) prefs.getString(KEY_LEVERAGE, "1x") ?: "1x" else "1x"

    fun saveLeverage(lev: String) {
        if (::prefs.isInitialized) prefs.edit().putString(KEY_LEVERAGE, lev).apply()
    }
}
