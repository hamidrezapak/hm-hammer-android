package com.example.ui.theme

import androidx.compose.runtime.compositionLocalOf
import com.example.ui.components.LanguageOption

val LocalAppLanguage = compositionLocalOf { LanguageOption.FA }

object AppLocale {
    fun t(key: String, lang: LanguageOption): String {
        return when (lang) {
            LanguageOption.FA -> faMap[key] ?: enMap[key] ?: key
            LanguageOption.AR -> arMap[key] ?: enMap[key] ?: key
            LanguageOption.EN -> enMap[key] ?: key
        }
    }

    private val enMap = mapOf(
        "tab_chart" to "CHART", "tab_trade" to "TRADE", "tab_ai" to "AI COPILOT",
        "tab_history" to "HISTORY", "tab_wallet" to "WALLET", "tab_admin" to "ADMIN",
        "tab_help" to "HELP", "tab_plans" to "PLANS", "tab_performance" to "PERFORMANCE"
    )

    private val faMap = mapOf(
        "tab_chart" to "نمودار", "tab_trade" to "معامله", "tab_ai" to "دستیار هوش‌مصنوعی",
        "tab_history" to "تاریخچه", "tab_wallet" to "کیف‌پول", "tab_admin" to "مدیریت",
        "tab_help" to "راهنما", "tab_plans" to "پلن‌ها", "tab_performance" to "عملکرد"
    )

    private val arMap = mapOf(
        "tab_chart" to "الرسم البياني", "tab_trade" to "التداول", "tab_ai" to "المساعد الذكي",
        "tab_history" to "السجل", "tab_wallet" to "المحفظة", "tab_admin" to "الإدارة",
        "tab_help" to "المساعدة", "tab_plans" to "الباقات", "tab_performance" to "الأداء"
    )
}
