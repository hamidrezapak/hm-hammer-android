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

        // Wallet
        "wallet_header" to "LIVE EXCHANGE ASSETS",
        "wallet_usdt" to "USDT Assets",
        "wallet_tmn" to "TMN Equivalent",
        "wallet_btc" to "BTC Holdings",
        "wallet_trx" to "TRX Reserve",
        "wallet_awaiting" to "Awaiting Live Exchange API Key",
        "wallet_zero_desc" to "No connected funds. Connect API key to sync balance.",
        "api_credentials" to "EXCHANGE API CREDENTIALS",
        "api_hint" to "Enter your Wallex, Nobitex, or BingX API Key",
        "save_api" to "SAVE & CONNECT EXCHANGE",

        // Plans
        "plans_title" to "SUBSCRIPTION TIERS & PERFORMANCE FEE",
        "plan_vip_desc" to "Priority high-speed batching • 0.0% platform fee • 24/7 dedicated anti-fragile engine",
        "plan_elite_desc" to "Up to 50 orders/batch • Dynamic ATR TP targets • Real-time alerts",
        "plan_pro_desc" to "Automated execution • 2% risk lock • Supported on all pairs",
        "upgrade_btn" to "ACTIVATE PLAN",

        // Help
        "help_title" to "ANTI-FRAGILE ENGINE PROTOCOL RULES",
        "rule_1_title" to "CANDLE GEOMETRY (HAMMER & SHOOTING STAR)",
        "rule_1_desc" to "Lower shadow >= 2.0x body, upper shadow <= 0.25x body for confirmation.",
        "rule_2_title" to "MACRO TREND FILTER (EMA 200)",
        "rule_2_desc" to "Long positions require Price > EMA200 for absolute trend alignment.",
        "rule_3_title" to "DYNAMIC STOP LOSS (ATR 1.5x)",
        "rule_3_desc" to "Stop loss dynamically updates with market volatility to protect equity."
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

        // Wallet
        "wallet_header" to "دارایی زنده صرافی متصل",
        "wallet_usdt" to "موجودی تتر (USDT)",
        "wallet_tmn" to "معادل تومان",
        "wallet_btc" to "موجودی بیت‌کوین (BTC)",
        "wallet_trx" to "ذخیره ترون (کارمزد شبکه)",
        "wallet_awaiting" to "در انتظار اتصال کلید API صرافی",
        "wallet_zero_desc" to "هیچ دارایی واریز نشده است. برای همگام‌سازی موجودی، کلید API را متصل کنید.",
        "api_credentials" to "اطلاعات و کلید API صرافی",
        "api_hint" to "کلید API صرافی والکس، نوبیتکس یا بینگ‌ایکس را وارد کنید",
        "save_api" to "ذخیره و اتصال زنده به صرافی",

        // Plans
        "plans_title" to "پلن‌های اشتراک و ساختار کارمزد معاملاتی",
        "plan_vip_desc" to "اولویت شماره یک در ثبت اردر • صفر درصد کارمزد پلتفرم • پشتیبانی ۲۴/۷ الگوریتمی",
        "plan_elite_desc" to "تا ۵۰ اردر همزمان • حد سود پویا با شاخص ATR • هشدارهای آنی رادار",
        "plan_pro_desc" to "ترید خودکار هوشمند • قفل ریسک ۲ درصد • پشتیبانی از کلیه جفت‌ارزها",
        "upgrade_btn" to "فعال‌سازی و ارتقا",

        // Help
        "help_title" to "قوانین و پروتکل استراتژی موتور معاملاتی",
        "rule_1_title" to "الگوی هندسی کندل (چکش و پین‌بار)",
        "rule_1_desc" to "سایه پایینی حداقل ۲ برابر بدنه و سایه بالایی کمتر از ۰.۲۵ بدنه برای تایید ورود.",
        "rule_2_title" to "فیلتر روند ماکرو (میانگین متحرک ۲۰۰)",
        "rule_2_desc" to "پوزیشن‌های خرید فقط در شرایط قیمت بالاتر از EMA200 اجرا می‌شوند.",
        "rule_3_title" to "حد ضرر داینامیک (ضریب ۱.۵ ATR)",
        "rule_3_desc" to "حد ضرر با نوسان واقعی بازار هماهنگ شده تا از سرمایه محافظت کامل شود."
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

        // Wallet
        "wallet_header" to "أصول المنصة المتصلة (حقيقي)",
        "wallet_usdt" to "رصيد التيثر (USDT)",
        "wallet_tmn" to "المعادل بالتومان",
        "wallet_btc" to "رصيد البيتكوين (BTC)",
        "wallet_trx" to "احتياطي شبكة ترون",
        "wallet_awaiting" to "بانتظار ربط مفتاح API للمنصة",
        "wallet_zero_desc" to "لا توجد أموال مربوطة. اربط مفتاح API لمزامنة الرصيد الحي.",
        "api_credentials" to "بيانات مفتاح API للمنصة",
        "api_hint" to "أدخل مفتاح API الخاص بـ Wallex أو Nobitex أو BingX",
        "save_api" to "حفظ والاتصال بالمنصة",

        // Plans
        "plans_title" to "باقات الاشتراك وهيكل عمولة التداول",
        "plan_vip_desc" to "أولوية قصوى لتنفيذ الأوامر • 0.0% عمولة المنصة • دعم خوارزمي متواصل",
        "plan_elite_desc" to "حتى 50 صفقة دفعة واحدة • أهداف ربح ديناميكية مع ATR • تنبيهات فورية",
        "plan_pro_desc" to "تداول آلي ذكي • قفل المخاطر بنسبة 2% • دعم لكافة الأزواج",
        "upgrade_btn" to "تفعيل الباقة",

        // Help
        "help_title" to "بروتوكول واستراتيجية المحرك المضاد للهشاشة",
        "rule_1_title" to "هندسة الشموع (المطرقة والنجم الساقط)",
        "rule_1_desc" to "الظل السفلي ضعف جسم الشمعة على الأقل لتأكيد نقطة الدخول.",
        "rule_2_title" to "فلتر الاتجاه العام (متوسط 200 EMA)",
        "rule_2_desc" to "صفقات الشراء تتطلب أن يكون السعر أعلى من خط EMA200 بالكامل.",
        "rule_3_title" to "وقف الخسارة الديناميكي (مضاعف 1.5x ATR)",
        "rule_3_desc" to "يتم تحديث وقف الخسارة تلقائياً مع تقلبات السوق لحماية رأس المال."
    )
}
