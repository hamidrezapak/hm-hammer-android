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
        "tab_chart" to "CHART", "tab_trade" to "TRADE", "tab_history" to "HISTORY",
        "tab_wallet" to "WALLET", "tab_admin" to "ADMIN", "tab_help" to "HELP",
        "tab_plans" to "PLANS", "tab_performance" to "PERFORMANCE",
        "engine_active" to "ENGINE ACTIVE", "engine_paused" to "ENGINE PAUSED",
        "net_profit_24h" to "Net Profit • Last 24 Hours", "atr_vol" to "ATR VOLATILITY",
        "btc_trend" to "BTC TREND", "bullish" to "BULLISH", "bearish" to "BEARISH",
        "vol_filter" to "VOLUME FILTER ACTIVE", "ema_trend" to "EMA(200) TREND LINE",
        "pairs_scanning" to "PAIRS SCANNING 15", "live_scanner" to "LIVE PULSE SCANNER",
        "auto_trade_title" to "AUTO-TRADING BOT & RISK ENGINE",
        "auto_trade_sub" to "Dynamic SL & TP targets active • Anti-Fragile Mode",
        "rr_ratio" to "R:R RATIO", "sl_mode" to "SL MODE", "batch_queue" to "BATCH QUEUE",
        "dyn_sl_title" to "DYNAMIC STOP-LOSS (SL) CONFIGURATION",
        "dyn_tp_title" to "DYNAMIC TAKE-PROFIT (TP) TARGETS",
        "trailing_lock" to "TRAILING BREAKEVEN LOCK",
        "trailing_desc" to "Auto-move Stop Loss to entry price when TP1 is hit",
        "live_price" to "Live Price", "sim_levels" to "LIVE TARGET LEVELS (BTC/USDT)",
        "matched_trades" to "MATCHED TRADES", "win_rate" to "WIN RATE", "filtered_pnl" to "FILTERED PNL",
        "filter_pair" to "FILTER BY ASSET PAIR", "tx_ledger" to "TRANSACTION LEDGER (LIVE SESSIONS)",
        "time_status" to "TIME / STATUS", "pnl_usdt" to "PNL / USDT", "entry_exit" to "ENTRY / EXIT", "pair_side" to "PAIR / SIDE",
        "wallet_header" to "LIVE EXCHANGE ASSETS", "wallet_usdt" to "USDT Assets", "wallet_tmn" to "TMN Equivalent",
        "wallet_btc" to "BTC Holdings", "wallet_trx" to "TRX Reserve", "wallet_awaiting" to "Awaiting Live Exchange API Key",
        "wallet_zero_desc" to "No connected funds. Connect API key to sync balance.",
        "api_credentials" to "EXCHANGE API CREDENTIALS", "api_hint" to "Enter your Wallex, Nobitex, or BingX API Key",
        "save_api" to "SAVE & CONNECT EXCHANGE",
        "plans_header" to "SUBSCRIPTION TIERS & PERFORMANCE FEE",
        "help_header" to "ANTI-FRAGILE STRATEGY PROTOCOL RULES"
    )

    private val faMap = mapOf(
        "tab_chart" to "نمودار", "tab_trade" to "معامله", "tab_history" to "تاریخچه",
        "tab_wallet" to "کیف‌پول", "tab_admin" to "مدیریت", "tab_help" to "راهنما",
        "tab_plans" to "پلن‌ها", "tab_performance" to "عملکرد",
        "engine_active" to "موتور معاملاتی فعال", "engine_paused" to "موتور متوقف",
        "net_profit_24h" to "سود خالص • ۲۴ ساعت گذشته", "atr_vol" to "نوسان بازار (ATR)",
        "btc_trend" to "روند بیت‌کوین", "bullish" to "صعودی", "bearish" to "نزولی",
        "vol_filter" to "فیلتر حجم فعال است", "ema_trend" to "خط روند میانگین ۲۰۰",
        "pairs_scanning" to "پایش ۱۵ جفت‌ارز", "live_scanner" to "اسکنر نبض زنده بازار",
        "auto_trade_title" to "ربات ترید خودکار و موتور مدیریت ریسک",
        "auto_trade_sub" to "موتور ضد شکنندگی: حد ضرر و سود داینامیک فعال",
        "rr_ratio" to "نسبت سود/زیان", "sl_mode" to "حالت حد ضرر", "batch_queue" to "صف اردرها",
        "dyn_sl_title" to "تنظیمات داینامیک حد ضرر (Stop-Loss)",
        "dyn_tp_title" to "اهداف داینامیک حد سود (Take-Profit)",
        "trailing_lock" to "قفل ریسک‌فری خودکار (Breakeven)",
        "trailing_desc" to "انتقال خودکار حد ضرر به نقطه ورود پس از لمس تارگت اول",
        "live_price" to "قیمت زنده", "sim_levels" to "سطوح قیمتی فعال (BTC/USDT)",
        "matched_trades" to "تعداد کل معاملات", "win_rate" to "نرخ برد (Win Rate)", "filtered_pnl" to "سود و زیان فیلترشده",
        "filter_pair" to "فیلتر بر اساس جفت‌ارز", "tx_ledger" to "دفتر کل معاملات زنده",
        "time_status" to "زمان / وضعیت", "pnl_usdt" to "سود و زیان / دلار", "entry_exit" to "ورود / خروج", "pair_side" to "جفت‌ارز / جهت",
        "wallet_header" to "دارایی زنده صرافی متصل", "wallet_usdt" to "موجودی تتر (USDT)", "wallet_tmn" to "معادل تومان",
        "wallet_btc" to "موجودی بیت‌کوین (BTC)", "wallet_trx" to "ذخیره ترون (کارمزد شبکه)", "wallet_awaiting" to "در انتظار اتصال کلید API صرافی",
        "wallet_zero_desc" to "هیچ دارایی واریز نشده است. برای همگام‌سازی موجودی، کلید API را متصل کنید.",
        "api_credentials" to "اطلاعات و کلید API صرافی", "api_hint" to "کلید API صرافی والکس، نوبیتکس یا بینگ‌ایکس را وارد کنید",
        "save_api" to "ذخیره و اتصال زنده به صرافی",
        "plans_header" to "پلن‌های اشتراک + ۲.۵٪ کارمزد سود",
        "help_header" to "پروتکل و قوانین استراتژی موتور معاملاتی"
    )

    private val arMap = mapOf(
        "tab_chart" to "الرسم البياني", "tab_trade" to "التداول", "tab_history" to "السجل",
        "tab_wallet" to "المحفظة", "tab_admin" to "الإدارة", "tab_help" to "المساعدة",
        "tab_plans" to "الباقات", "tab_performance" to "الأداء",
        "engine_active" to "محرك التداول نشط", "engine_paused" to "المحرك متوقف",
        "net_profit_24h" to "صافي الأرباح • آخر 24 ساعة", "atr_vol" to "مؤشر التقلب (ATR)",
        "btc_trend" to "اتجاه البيتكوين", "bullish" to "صاعد", "bearish" to "هابط",
        "vol_filter" to "فلتر السيولة والحجم نشط", "ema_trend" to "خط اتجاه المتوسط 200",
        "pairs_scanning" to "مراقبة 15 زوج تداول", "live_scanner" to "الماسح الحي لنبض السوق",
        "auto_trade_title" to "روبوت التداول الآلي وإدارة المخاطر",
        "auto_trade_sub" to "المحرك المضاد للهشاشة: وقف الخسارة وجني الأرباح نشط",
        "rr_ratio" to "نسبة العائد/المخاطرة", "sl_mode" to "نمط وقف الخسارة", "batch_queue" to "طابور الأوامر",
        "dyn_sl_title" to "إعدادات وقف الخسارة الديناميكي (SL)",
        "dyn_tp_title" to "أهداف جني الأرباح الديناميكية (TP)",
        "trailing_lock" to "قفل نقطة الدخول (بدون مخاطرة)",
        "trailing_desc" to "نقل وقف الخسارة تلقائياً لسعر الدخول عند تحقيق الهدف الأول",
        "live_price" to "السعر المباشر", "sim_levels" to "مستويات التداول الحية (BTC/USDT)",
        "matched_trades" to "إجمالي الصفقات", "win_rate" to "نسبة النجاح", "filtered_pnl" to "الأرباح والخسائر المفلترة",
        "filter_pair" to "تصفية حسب الزوج", "tx_ledger" to "سجل الصفقات الحية",
        "time_status" to "الوقت / الحالة", "pnl_usdt" to "الربح / USDT", "entry_exit" to "دخول / خروج", "pair_side" to "الزوج / النوع",
        "wallet_header" to "أصول المنصة المتصلة (حقيقي)", "wallet_usdt" to "رصيد التيثر (USDT)", "wallet_tmn" to "المعادل بالتومان",
        "wallet_btc" to "رصيد البيتكوين (BTC)", "wallet_trx" to "احتياطي شبكة ترون", "wallet_awaiting" to "بانتظار ربط مفتاح API للمنصة",
        "wallet_zero_desc" to "لا توجد أموال مربوطة. اربط مفتاح API لمزامنة الرصيد الحي.",
        "api_credentials" to "بيانات مفتاح API للمنصة", "api_hint" to "أدخل مفتاح API الخاص بـ Wallex أو Nobitex أو BingX",
        "save_api" to "حفظ والاتصال بالمنصة",
        "plans_header" to "باقات الاشتراك + 2.5% عمولة أرباح",
        "help_header" to "بروتوكول واستراتيجية المحرك المضاد للهشاشة"
    )
}
