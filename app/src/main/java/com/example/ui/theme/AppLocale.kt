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
        "tab_chart" to "CHART", "tab_trade" to "TRADE", "tab_history" to "HISTORY",
        "tab_wallet" to "WALLET", "tab_admin" to "ADMIN", "tab_help" to "HELP",
        "tab_plans" to "PLANS", "tab_performance" to "PERFORMANCE",
        "net_profit_24h" to "Net Profit • Last 24 Hours", "atr_volatility" to "ATR VOLATILITY",
        "btc_trend" to "BTC TREND", "bullish" to "BULLISH", "bearish" to "BEARISH",
        "vol_filter_active" to "VOLUME FILTER ACTIVE", "ema_trend_line" to "EMA(200) TREND LINE",
        "pairs_scanning" to "PAIRS SCANNING 15", "live_pulse_scanner" to "LIVE PULSE SCANNER",
        "auto_trading_title" to "AUTO-TRADING BOT & RISK ENGINE",
        "auto_trading_sub" to "Anti-Fragile Engine: Dynamic SL & TP targets active",
        "rr_ratio" to "R:R RATIO", "sl_mode" to "SL MODE", "batch_queue" to "BATCH QUEUE",
        "adv_risk_mgmt" to "ADVANCED RISK MANAGEMENT", "dyn_sl_config" to "DYNAMIC STOP-LOSS (SL) CONFIGURATION",
        "dyn_tp_config" to "DYNAMIC TAKE-PROFIT (TP) TARGETS", "trailing_lock" to "TRAILING BREAKEVEN LOCK",
        "trailing_desc" to "Auto-move Stop Loss to entry price when TP1 is hit",
        "live_price" to "Live Price", "sim_levels" to "LIVE TARGET LEVELS (BTC/USDT)",
        "matched_trades" to "MATCHED TRADES", "win_rate" to "WIN RATE", "filtered_pnl" to "FILTERED PNL",
        "filter_asset_pair" to "FILTER BY ASSET PAIR", "time_status" to "TIME / STATUS",
        "pnl_usdt" to "PNL / USDT", "entry_exit" to "ENTRY / EXIT", "pair_side" to "PAIR / SIDE",
        "radar_terminal" to "RADAR ENGINE TERMINAL (v2.0)", "recent_exec" to "RECENT EXECUTIONS & DYNAMIC ATR TP TARGETS"
    )

    private val faTranslations = mapOf(
        "tab_chart" to "نمودار", "tab_trade" to "معامله", "tab_history" to "تاریخچه",
        "tab_wallet" to "کیف‌پول", "tab_admin" to "مدیریت", "tab_help" to "راهنما",
        "tab_plans" to "پلن‌ها", "tab_performance" to "عملکرد",
        "net_profit_24h" to "سود خالص • ۲۴ ساعت گذشته", "atr_volatility" to "نوسان بازار (ATR)",
        "btc_trend" to "روند بیت‌کوین", "bullish" to "صعودی", "bearish" to "نزولی",
        "vol_filter_active" to "فیلتر حجم فعال است", "ema_trend_line" to "خط روند میانگین ۲۰۰",
        "pairs_scanning" to "پایش ۱۵ جفت‌ارز", "live_pulse_scanner" to "اسکنر نبض زنده بازار",
        "auto_trading_title" to "ربات ترید خودکار و موتور مدیریت ریسک",
        "auto_trading_sub" to "موتور ضد شکنندگی: حد ضرر و سود داینامیک فعال",
        "rr_ratio" to "نسبت سود/زیان", "sl_mode" to "حالت حد ضرر", "batch_queue" to "صف اردرها",
        "adv_risk_mgmt" to "مدیریت پیشرفته ریسک و سرمایه", "dyn_sl_config" to "تنظیمات داینامیک حد ضرر (SL)",
        "dyn_tp_config" to "اهداف داینامیک حد سود (TP)", "trailing_lock" to "قفل ریسک‌فری خودکار (Breakeven)",
        "trailing_desc" to "انتقال خودکار حد ضرر به نقطه ورود با لمس تارگت ۱",
        "live_price" to "قیمت زنده", "sim_levels" to "سطوح قیمتی فعال (BTC/USDT)",
        "matched_trades" to "تعداد کل معاملات", "win_rate" to "نرخ برد (Win Rate)", "filtered_pnl" to "سود و زیان فیلترشده",
        "filter_asset_pair" to "فیلتر بر اساس جفت‌ارز", "time_status" to "زمان / وضعیت",
        "pnl_usdt" to "سود و زیان / دلار", "entry_exit" to "ورود / خروج", "pair_side" to "جفت‌ارز / جهت",
        "radar_terminal" to "ترمینال و کنسول رادار معاملاتی (نسخه ۲)", "recent_exec" to "معاملات اخیر و تارگت‌های فعال ATR"
    )

    private val arTranslations = mapOf(
        "tab_chart" to "الرسم البياني", "tab_trade" to "التداول", "tab_history" to "السجل",
        "tab_wallet" to "المحفظة", "tab_admin" to "الإدارة", "tab_help" to "المساعدة",
        "tab_plans" to "الباقات", "tab_performance" to "الأداء",
        "net_profit_24h" to "صافي الأرباح • آخر 24 ساعة", "atr_volatility" to "مؤشر التقلب (ATR)",
        "btc_trend" to "اتجاه البيتكوين", "bullish" to "صاعد", "bearish" to "هابط",
        "vol_filter_active" to "فلتر السيولة والحجم نشط", "ema_trend_line" to "خط اتجاه المتوسط 200",
        "pairs_scanning" to "مراقبة 15 زوج تداول", "live_pulse_scanner" to "الماسح الحي لنبض السوق",
        "auto_trading_title" to "روبوت التداول الآلي وإدارة المخاطر",
        "auto_trading_sub" to "المحرك المضاد للهشاشة: وقف الخسارة وجني الأرباح نشط",
        "rr_ratio" to "نسبة العائد/المخاطرة", "sl_mode" to "نمط وقف الخسارة", "batch_queue" to "طابور الأوامر",
        "adv_risk_mgmt" to "الإدارة المتقدمة للمخاطر", "dyn_sl_config" to "إعدادات وقف الخسارة الديناميكي (SL)",
        "dyn_tp_config" to "أهداف جني الأرباح الديناميكية (TP)", "trailing_lock" to "قفل نقطة الدخول (بدون مخاطرة)",
        "trailing_desc" to "نقل وقف الخسارة تلقائياً لسعر الدخول عند تحقيق الهدف 1",
        "live_price" to "السعر المباشر", "sim_levels" to "مستويات التداول الحية (BTC/USDT)",
        "matched_trades" to "إجمالي الصفقات", "win_rate" to "نسبة النجاح", "filtered_pnl" to "الأرباح والخسائر المفلترة",
        "filter_asset_pair" to "تصفية حسب الزوج", "time_status" to "الوقت / الحالة",
        "pnl_usdt" to "الربح / USDT", "entry_exit" to "دخول / خروج", "pair_side" to "الزوج / النوع",
        "radar_terminal" to "محطة رادار محرك التداول (v2.0)", "recent_exec" to "الصفقات المنفذة حديثاً وأهداف ATR"
    )
}
