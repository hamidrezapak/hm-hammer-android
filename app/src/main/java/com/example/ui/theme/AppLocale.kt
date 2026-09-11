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
        "tab_help" to "HELP", "tab_plans" to "PLANS", "tab_performance" to "PERFORMANCE",

        "wallet_header" to "Assets Overview",
        "wallet_usdt" to "USDT",
        "wallet_tmn" to "Toman (TMN)",
        "wallet_btc" to "Bitcoin (BTC)",
        "wallet_trx" to "Tron (TRX)",
        "wallet_awaiting" to "Awaiting Connection",
        "api_credentials" to "API Credentials",
        "wallet_zero_desc" to "Enter your Wallex API key to view your real balance and enable trading.",
        "api_hint" to "Enter your API key here...",
        "save_api" to "Save & Connect"
    )

    private val faMap = mapOf(
        "tab_chart" to "نمودار", "tab_trade" to "معامله", "tab_ai" to "دستیار هوش‌مصنوعی",
        "tab_history" to "تاریخچه", "tab_wallet" to "کیف‌پول", "tab_admin" to "مدیریت",
        "tab_help" to "راهنما", "tab_plans" to "پلن‌ها", "tab_performance" to "عملکرد",

        "wallet_header" to "بررسی اجمالی دارایی‌ها",
        "wallet_usdt" to "تتر (USDT)",
        "wallet_tmn" to "تومان",
        "wallet_btc" to "بیت‌کوین",
        "wallet_trx" to "ترون",
        "wallet_awaiting" to "در انتظار اتصال",
        "api_credentials" to "اطلاعات اتصال API",
        "wallet_zero_desc" to "کلید API صرافی والکس خود را وارد کنید تا موجودی واقعی نمایش داده شود و معاملات فعال شود.",
        "api_hint" to "کلید API را اینجا وارد کنید...",
        "save_api" to "ذخیره و اتصال"
    )

    private val arMap = mapOf(
        "tab_chart" to "الرسم البياني", "tab_trade" to "التداول", "tab_ai" to "المساعد الذكي",
        "tab_history" to "السجل", "tab_wallet" to "المحفظة", "tab_admin" to "الإدارة",
        "tab_help" to "المساعدة", "tab_plans" to "الباقات", "tab_performance" to "الأداء",

        "wallet_header" to "نظرة عامة على الأصول",
        "wallet_usdt" to "USDT",
        "wallet_tmn" to "تومان",
        "wallet_btc" to "بيتكوين",
        "wallet_trx" to "ترون",
        "wallet_awaiting" to "بانتظار الاتصال",
        "api_credentials" to "بيانات API",
        "wallet_zero_desc" to "أدخل مفتاح API الخاص بك في والكس لعرض رصيدك الحقيقي وتفعيل التداول.",
        "api_hint" to "أدخل مفتاح API هنا...",
        "save_api" to "حفظ واتصال"
    )
}
