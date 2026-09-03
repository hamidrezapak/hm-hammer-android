#!/bin/bash
set -e

echo "🚀 [Skill Engine] شروع اعمال اسکیل UI/UX و سیستم زبان سراسری..."

# ۱. ساخت زیرساخت سراسری زبان و تم (AppLocale & Language Engine)
cat << 'LOCALE_EOF' > app/src/main/java/com/example/ui/theme/AppLocale.kt
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
LOCALE_EOF

# ۲. بازنویسی کامپوننت چارت با قابلیت گوش دادن به زبان زنده
cat << 'CHART_EOF' > app/src/main/java/com/example/ui/screens/ChartRadarScreen.kt
package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppLocale
import com.example.ui.theme.LocalAppLanguage
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ChartRadarScreen(
    viewModel: MainViewModel
) {
    val lang = LocalAppLanguage.current
    var selectedTf by remember { mutableStateOf("15M") }
    var selectedPair by remember { mutableStateOf("BTC/USDT") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                border = BorderStroke(1.dp, Color(0xFF30363D)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text("ANTI-FRAGILE PNL", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("+12.8%", color = Color(0xFF00E676), fontSize = 28.sp, fontWeight = FontWeight.Black)
                    Text(AppLocale.t("net_profit_24h", lang), color = Color.LightGray, fontSize = 11.sp)
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                    border = BorderStroke(1.dp, Color(0xFF30363D)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(AppLocale.t("atr_vol", lang), color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("x 5.42", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                    border = BorderStroke(1.dp, Color(0xFF30363D)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(AppLocale.t("btc_trend", lang), color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(AppLocale.t("bullish", lang), color = Color(0xFF00E676), fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("1D", "4H", "1H", "15M").forEach { tf ->
                    val isSel = selectedTf == tf
                    Button(
                        onClick = { selectedTf = tf },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isSel) Color(0xFF00E676) else Color(0xFF21262D)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(36.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(tf, color = if (isSel) Color.Black else Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                border = BorderStroke(1.dp, Color(0xFF30363D)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("$92,352.64", color = Color(0xFFFF5252), fontSize = 16.sp, fontWeight = FontWeight.Black)
                        Surface(color = Color(0xFF00E676).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                            Text("$selectedTf $selectedPair", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val candleW = w / 15f
                            for (i in 0 until 14) {
                                val isUp = i % 2 == 0
                                val color = if (isUp) Color(0xFF00E676) else Color(0xFFFF5252)
                                val x = i * candleW + 10f
                                val top = (h * 0.2f) + (i * 4f % (h * 0.4f))
                                val btm = top + (h * 0.3f)
                                drawLine(color, Offset(x + candleW / 4, top - 15), Offset(x + candleW / 4, btm + 15), strokeWidth = 2f)
                                drawRect(color, Offset(x, top), Size(candleW / 2, btm - top))
                            }
                            val path = Path()
                            path.moveTo(0f, h * 0.7f)
                            path.quadraticBezierTo(w * 0.5f, h * 0.6f, w, h * 0.25f)
                            drawPath(path, Color(0xFFFFB703), style = Stroke(width = 3f))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(AppLocale.t("vol_filter", lang), color = Color.Gray, fontSize = 9.sp)
                        Text(AppLocale.t("ema_trend", lang), color = Color(0xFFFFB703), fontSize = 9.sp)
                    }
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(AppLocale.t("pairs_scanning", lang), color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(AppLocale.t("live_scanner", lang), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("BTC/USDT" to "$92,517.25", "ETH/USDT" to "$3,259.01", "SOL/USDT" to "$194.19", "TRX/USDT" to "$0.23").forEach { (pair, price) ->
                    val isSel = selectedPair == pair
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) Color(0xFF00E676).copy(alpha = 0.15f) else Color(0xFF21262D),
                        border = BorderStroke(1.dp, if (isSel) Color(0xFF00E676) else Color(0xFF30363D)),
                        modifier = Modifier.clickable { selectedPair = pair }
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Text(pair, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(price, color = Color(0xFF00E676), fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
CHART_EOF

# ۳. تنظیم ریشه MainActivity برای تزریق یکپارچه LocalAppLanguage
cat << 'MAIN_EOF' > app/src/main/java/com/example/MainActivity.kt
package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.ui.components.AppTabBar
import com.example.ui.components.HeaderPulseBar
import com.example.ui.components.LanguageOption
import com.example.ui.screens.*
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.LocalAppLanguage
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var selectedLanguage by remember { mutableStateOf(LanguageOption.FA) }
            val isRtl = selectedLanguage == LanguageOption.FA || selectedLanguage == LanguageOption.AR
            val layoutDirection = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(
                LocalLayoutDirection provides layoutDirection,
                LocalAppLanguage provides selectedLanguage
            ) {
                MainAppScreen(
                    viewModel = viewModel,
                    currentLanguage = selectedLanguage,
                    onLanguageChanged = { selectedLanguage = it }
                )
            }
        }
    }
}

@Composable
fun MainAppScreen(
    viewModel: MainViewModel,
    currentLanguage: LanguageOption = LanguageOption.FA,
    onLanguageChanged: (LanguageOption) -> Unit = {}
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val isPulseAlive by viewModel.isRadarPulseAlive.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg),
        containerColor = DarkNavyBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HeaderPulseBar(
                isAlive = isPulseAlive,
                isPulseAlive = isPulseAlive,
                currentLanguage = currentLanguage,
                onLanguageSelected = onLanguageChanged
            )

            AppTabBar(
                selectedTab = currentTab,
                onTabSelected = { viewModel.setTab(it) },
                currentLanguage = currentLanguage,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )

            when (currentTab) {
                AppTab.CHART -> ChartRadarScreen(viewModel = viewModel)
                AppTab.TRADE -> TradeScreen(viewModel = viewModel)
                AppTab.HISTORY -> TransactionHistoryScreen(viewModel = viewModel)
                AppTab.WALLET -> WalletScreen(viewModel = viewModel)
                AppTab.PERFORMANCE -> PerformanceScreen(viewModel = viewModel)
                AppTab.SUBSCRIPTIONS -> SubscriptionsScreen(viewModel = viewModel)
                AppTab.HELP -> HelpGuideScreen()
                AppTab.ADMIN -> AdminScreen(viewModel = viewModel)
            }
        }
    }
}
MAIN_EOF

echo "✅ تمام تنظیمات اعمال شد. در حال ارسال به گیت‌هاب..."
git add app/src/main/java/com/example/ui/theme/AppLocale.kt \
        app/src/main/java/com/example/ui/screens/ChartRadarScreen.kt \
        app/src/main/java/com/example/MainActivity.kt

git commit -m "Apply automated UI/UX & Global CompositionLocal Language Engine skill"
git push origin main
echo "🎉 عملیات ارسال با موفقیت به پایان رسید!"
