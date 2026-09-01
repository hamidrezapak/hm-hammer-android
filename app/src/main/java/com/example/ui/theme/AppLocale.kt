package com.example.ui.theme

import com.example.ui.components.LanguageOption

object AppLocale {
    fun t(key: String, lang: LanguageOption): String {
        return when (lang) {
            LanguageOption.FA -> faTranslations[key] ?: enTranslations[key] ?: key
            LanguageOption.AR -> arTranslations[key] ?: enTranslations[key] ?: key
            LanguageOption.EN -> enTranslations[key] ?: key
        }
    }

    private val enTranslations = mapOf(
        // Tabs
        "tab_chart" to "CHART",
        "tab_trade" to "TRADE",
        "tab_history" to "HISTORY",
        "tab_wallet" to "WALLET",
        "tab_admin" to "ADMIN",
        "tab_help" to "HELP",
        "tab_plans" to "PLANS",
        "tab_performance" to "PERFORMANCE",

        // Header & Engine
        "engine_active" to "ENGINE ACTIVE",
        "engine_paused" to "ENGINE PAUSED",

        // Security & Admin
        "security_title" to "100% Non-Custodial Architecture",
        "security_desc" to "Zero withdrawal permissions. Funds stay safely in your exchange. API keys encrypted locally via AES-256.",
        "panic_btn" to "EMERGENCY PANIC - CLOSE ALL POSITIONS",
        "panic_activated" to "PANIC ACTIVATED - TRADING HALTED",
        "plans_header" to "Subscription Plans & 2.5% Profit Share",
        "supported_exchanges" to "Supported Exchanges (Spot / Futures)",
        "connected_accounts" to "Connected Exchange API Accounts",
        "status_awaiting" to "Awaiting Live Exchange API Key",
        "active_exchanges" to "Active Exchanges"
    )

    private val faTranslations = mapOf(
        // Tabs
        "tab_chart" to "نمودار",
        "tab_trade" to "معامله",
        "tab_history" to "تاریخچه",
        "tab_wallet" to "کیف‌پول",
        "tab_admin" to "مدیریت",
        "tab_help" to "راهنما",
        "tab_plans" to "پلن‌ها",
        "tab_performance" to "عملکرد",

        // Header & Engine
        "engine_active" to "موتور معاملاتی فعال",
        "engine_paused" to "موتور متوقف",

        // Security & Admin
        "security_title" to "معماری ۱۰۰٪ غیرحضانتی (Non-Custodial)",
        "security_desc" to "دارایی در صرافی خودتان است. ربات دسترسی برداشت ندارد و کلیدها با رمزنگاری AES-256 در موبایل ذخیره می‌شوند.",
        "panic_btn" to "دکمه توقف اضطراری و بستن پوزیشن‌ها",
        "panic_activated" to "دستور لغو تمام اردرها ارسال شد (فعال)",
        "plans_header" to "پلن‌های اشتراک + کارمزد ۲.۵٪ سود",
        "supported_exchanges" to "صرافی‌های متصل (اسپات / فیوچرز)",
        "connected_accounts" to "حساب‌های متصل به API صرافی",
        "status_awaiting" to "در انتظار اتصال کلید API صرافی",
        "active_exchanges" to "صرافی‌های فعال"
    )

    private val arTranslations = mapOf(
        // Tabs
        "tab_chart" to "الرسم البياني",
        "tab_trade" to "التداول",
        "tab_history" to "السجل",
        "tab_wallet" to "المحفظة",
        "tab_admin" to "الإدارة",
        "tab_help" to "المساعدة",
        "tab_plans" to "الباقات",
        "tab_performance" to "الأداء",

        // Header & Engine
        "engine_active" to "محرك التداول نشط",
        "engine_paused" to "المحرك متوقف",

        // Security & Admin
        "security_title" to "أمان غير احتجازي بنسبة 100٪",
        "security_desc" to "أموالك تبقى بأمان في منصتك. لا توجد صلاحيات سحب والمفاتيح مشفرة محلياً بواسطة AES-256.",
        "panic_btn" to "إيقاف اضطراري وإغلاق كافة الصفقات",
        "panic_activated" to "تم تفعيل الإيقاف الاضطراري وإلغاء الأوامر",
        "plans_header" to "باقات الاشتراك + 2.5% عمولة أرباح",
        "supported_exchanges" to "المنصات المدعومة (فوري / عقود)",
        "connected_accounts" to "حسابات التداول المتصلة بـ API",
        "status_awaiting" to "بانتظار ربط مفتاح API للمنصة",
        "active_exchanges" to "المنصات النشطة"
    )
}
