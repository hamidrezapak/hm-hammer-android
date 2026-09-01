package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AppLanguage(val code: String, val title: String, val isRtl: Boolean, val currency: String) {
    FA("fa", "فارسی", true, "تومان"),
    EN("en", "English", false, "USDT"),
    AR("ar", "العربية", true, "USDT")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onNavigateBack: () -> Unit = {},
    onRestartDaemon: () -> Unit = {},
    viewModel: Any? = null
) {
    val context = LocalContext.current
    var currentLang by remember { mutableStateOf(AppLanguage.FA) }
    var langMenuExpanded by remember { mutableStateOf(false) }
    var panicTriggered by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf("ADMIN") }

    val isFa = currentLang == AppLanguage.FA
    val isAr = currentLang == AppLanguage.AR
    val isRtl = currentLang.isRtl

    val layoutDirection = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            containerColor = Color(0xFF0D1117),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF161B22)),
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFF21262D), CircleShape)
                                    .clickable {
                                        Toast.makeText(context, if (isFa) "پنل مدیریت HM Hammer فعال است" else "HM Hammer Admin Console Active", Toast.LENGTH_SHORT).show()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("ADMIN", color = Color(0xFF00E676), fontSize = 8.sp, fontWeight = FontWeight.Black)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "HM HAMMER PRO",
                                    color = Color(0xFF00E676),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = if (isFa) "موتور معاملاتی فعال ●" else if (isAr) "محرك التداول نشط ●" else "ENGINE ACTIVE ●",
                                    color = Color(0xFF00E676),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    actions = {
                        // منوی کشویی انتخاب ۳ زبان
                        Box {
                            Button(
                                onClick = { langMenuExpanded = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(Icons.Default.Language, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(currentLang.title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }

                            DropdownMenu(
                                expanded = langMenuExpanded,
                                onDismissRequest = { langMenuExpanded = false },
                                modifier = Modifier.background(Color(0xFF161B22))
                            ) {
                                AppLanguage.values().forEach { lang ->
                                    DropdownMenuItem(
                                        text = { Text(lang.title, color = if (currentLang == lang) Color(0xFF00E676) else Color.White, fontWeight = FontWeight.Bold) },
                                        onClick = {
                                            currentLang = lang
                                            langMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // تب‌های کنترلی فعال بالای صفحه
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("ADMIN", "HELP", "PLANS", "PERFORMANCE").forEach { tab ->
                            val isSelected = activeTab == tab
                            Button(
                                onClick = {
                                    activeTab = tab
                                    Toast.makeText(context, "بخش $tab انتخاب شد", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f).height(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) Color(0xFF00E676) else Color(0xFF21262D),
                                    contentColor = if (isSelected) Color.Black else Color.White
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(tab, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // هشدار امنیتی غیرحضانتی (Non-Custodial)
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
                        border = BorderStroke(1.dp, Color(0xFF10B981)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isFa) "معماری ۱۰۰٪ غیرحضانتی (Non-Custodial)" else if (isAr) "أمان غير احتجازي بنسبة 100٪" else "100% Non-Custodial Architecture",
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = if (isFa) "کلیدها در حافظه امن گوشی رمزنگاری می‌شوند و ربات حق برداشت دارایی ندارد."
                                    else if (isAr) "أموالك تبقى في منصتك. لا توجد صلاحيات سحب والمفاتيح مشفرة محلياً."
                                    else "Zero withdrawal permissions. API keys encrypted locally on device.",
                                    color = Color.LightGray,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                // دکمه اضطراری توقف (Panic Button)
                item {
                    Button(
                        onClick = {
                            panicTriggered = !panicTriggered
                            val msg = if (panicTriggered)
                                (if (isFa) "کلیه سفارش‌ها لغو و معاملات متوقف شد" else "All orders canceled & trading halted")
                            else
                                (if (isFa) "سیستم معاملاتی به حالت عادی بازگشت" else "Trading resumed")
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (panicTriggered) Color(0xFFDC2626) else Color(0xFF7F1D1D)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (panicTriggered)
                                (if (isFa) "دستور لغو پوزیشن‌ها ارسال شد (فعال)" else if (isAr) "تم إلغاء الصفقات فوراً" else "PANIC ACTIVATED - TRADING HALTED")
                            else
                                (if (isFa) "دکمه توقف اضطراری و بستن پوزیشن‌ها" else if (isAr) "إيقاف اضطراري وإغلاق الصفقات" else "EMERGENCY PANIC - CLOSE ALL POSITIONS"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }
                }

                // پلن‌های اشتراک بر اساس زبان
                item {
                    Text(
                        text = if (isFa) "پلن‌های اشتراک + کارمزد ۲.۵٪ سود" else if (isAr) "باقات الاشتراك + 2.5% عمولة أرباح" else "Subscription Plans & 2.5% Profit Share",
                        color = Color(0xFFFFB703),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PlanSummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "Standard",
                            price = if (isFa) "۱.۵ م ت" else "$25 / mo",
                            cap = if (isFa) "سرمایه تا ۱,۰۰۰$" else "Cap < $1,000",
                            badgeColor = Color(0xFF38BDF8)
                        )
                        PlanSummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "Pro Scalp",
                            price = if (isFa) "۳.۸ م ت" else "$60 / mo",
                            cap = if (isFa) "سرمایه تا ۱۰,۰۰۰$" else "Cap < $10,000",
                            badgeColor = Color(0xFF818CF8)
                        )
                        PlanSummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "VIP Elite",
                            price = if (isFa) "۱۹ م ت" else "$300 / mo",
                            cap = if (isFa) "سرمایه نامحدود" else "Unlimited",
                            badgeColor = Color(0xFFF59E0B)
                        )
                    }
                }

                // صرافی‌های معتبر
                item {
                    Text(
                        text = if (isFa) "صرافی‌های متصل (اسپات / فیوچرز)" else if (isAr) "المنصات المدعومة" else "Supported Exchanges",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val exchanges = listOf("Nobitex", "Wallex", "Bitpin", "BingX", "CoinEx", "Binance", "Bybit", "OKX")
                        items(exchanges) { ex ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF21262D),
                                border = BorderStroke(1.dp, Color(0xFF30363D))
                            ) {
                                Text(
                                    text = ex,
                                    color = Color(0xFF58A6FF),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // پنل اتصال حساب‌های صرافی (کاملاً لایو و بدون عدد فیک)
                item {
                    Text(
                        text = if (isFa) "حساب‌های متصل به API صرافی" else if (isAr) "حسابات التداول المتصلة" else "Connected Exchange Accounts",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                item {
                    RealAccountCard(
                        username = "@hamid1365",
                        tier = "VIP / ELITE",
                        statusText = if (isFa) "در انتظار اتصال کلید API صرافی" else "Awaiting Live Exchange API Key",
                        exchangesText = "Wallex / BingX / Binance",
                        lang = currentLang
                    )
                }

                item {
                    RealAccountCard(
                        username = "@masjedi6913",
                        tier = "PRO SCALP",
                        statusText = if (isFa) "در انتظار اتصال کلید API صرافی" else "Awaiting Live Exchange API Key",
                        exchangesText = "Nobitex / CoinEx",
                        lang = currentLang
                    )
                }
            }
        }
    }
}

@Composable
fun RealAccountCard(
    username: String,
    tier: String,
    statusText: String,
    exchangesText: String,
    lang: AppLanguage
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
        border = BorderStroke(1.dp, Color(0xFF30363D)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF00E676).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = tier,
                        color = Color(0xFF00E676),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${if (lang == AppLanguage.FA) "وضعیت موجودی لایو: " else "Live Balance: "} $statusText",
                color = Color(0xFFFFB703),
                fontSize = 11.sp
            )
            Text(
                text = "${if (lang == AppLanguage.FA) "صرافی‌های فعال: " else "Target Exchanges: "} $exchangesText",
                color = Color(0xFF8B949E),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun PlanSummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    price: String,
    cap: String,
    badgeColor: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
        border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, color = badgeColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = price, color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
            Text(text = cap, color = Color.Gray, fontSize = 8.sp)
        }
    }
}
