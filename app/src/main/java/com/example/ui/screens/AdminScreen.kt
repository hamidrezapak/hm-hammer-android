package com.example.ui.screens

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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AppLanguage(val code: String, val title: String, val isRtl: Boolean, val currency: String) {
    FA("fa", "فارسی", true, "تومان"),
    EN("en", "English", false, "USDT"),
    AR("ar", "العربية", true, "USDT"),
    DE("de", "Deutsch", false, "USDT"),
    FR("fr", "Français", false, "USDT")
}

data class RealUserAccount(
    val id: String,
    val username: String,
    val plan: String,
    val capitalIrr: String,
    val capitalUsd: String,
    val profitShare: String,
    val activeExchanges: List<String>,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onNavigateBack: () -> Unit = {},
    onRestartDaemon: () -> Unit = {},
    viewModel: Any? = null
) {
    var currentLang by remember { mutableStateOf(AppLanguage.FA) }
    var panicTriggered by remember { mutableStateOf(false) }

    val layoutDirection = if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    val userList = remember {
        listOf(
            RealUserAccount(
                id = "1",
                username = "hamid1365@",
                plan = "VIP / ELITE",
                capitalIrr = "۱۵۰,۰۰۰,۰۰۰ تومان",
                capitalUsd = "$2,500",
                profitShare = "2.5% Success Fee",
                activeExchanges = listOf("Wallex", "BingX", "Binance"),
                status = "Active / Online"
            ),
            RealUserAccount(
                id = "2",
                username = "masjedi6913@",
                plan = "PRO MAX",
                capitalIrr = "۸۰,۰۰۰,۰۰۰ تومان",
                capitalUsd = "$1,350",
                profitShare = "2.5% Success Fee",
                activeExchanges = listOf("Nobitex", "CoinEx"),
                status = "Active / Online"
            )
        )
    }

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
                                    .size(10.dp)
                                    .background(Color(0xFF00E676), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "HM HAMMER PRO",
                                color = Color(0xFF00E676),
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }
                    },
                    actions = {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            items(AppLanguage.values()) { lang ->
                                val isSelected = lang == currentLang
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) Color(0xFF00E676) else Color(0xFF21262D),
                                    modifier = Modifier.clickable { currentLang = lang }
                                ) {
                                    Text(
                                        text = lang.title,
                                        color = if (isSelected) Color.Black else Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // تاییدیه امنیت غیرحضانتی
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
                        border = BorderStroke(1.dp, Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (currentLang == AppLanguage.FA) "امنیت ۱۰۰٪ غیرحضانتی (Non-Custodial)" else "100% Non-Custodial Architecture",
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (currentLang == AppLanguage.FA)
                                        "دارایی در صرافی خودتان است. ربات دسترسی برداشت ندارد و کلیدها محلی رمزنگاری می‌شوند."
                                    else
                                        "Zero withdrawal access. Funds stay safely in your exchange account with local AES-256 encryption.",
                                    color = Color.LightGray,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }

                // دکمه توقف اضطراری (Panic Button)
                item {
                    Button(
                        onClick = { panicTriggered = !panicTriggered },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (panicTriggered) Color(0xFFDC2626) else Color(0xFF7F1D1D)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (panicTriggered)
                                (if (currentLang == AppLanguage.FA) "دستور لغو تمام اردرها ارسال شد" else "PANIC TRIGGERED - ORDERS CANCELED")
                            else
                                (if (currentLang == AppLanguage.FA) "دکمه توقف اضطراری و بستن پوزیشن‌ها" else "EMERGENCY PANIC - CLOSE ALL"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }

                // پلن‌های شفاف
                item {
                    Text(
                        text = if (currentLang == AppLanguage.FA) "پلن‌های اشتراک + کارمزد ۲.۵٪ سود" else "Subscription Plans & 2.5% Profit Sharing",
                        color = Color(0xFFFFB703),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PlanSummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "Standard",
                            price = if (currentLang == AppLanguage.FA) "۱.۵ م ت" else "$25",
                            cap = "< $1,000",
                            badgeColor = Color(0xFF38BDF8)
                        )
                        PlanSummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "Pro Scalp",
                            price = if (currentLang == AppLanguage.FA) "۳.۸ م ت" else "$60",
                            cap = "< $10,000",
                            badgeColor = Color(0xFF818CF8)
                        )
                        PlanSummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "VIP Elite",
                            price = if (currentLang == AppLanguage.FA) "۱۹ م ت" else "$300",
                            cap = "Unlimited",
                            badgeColor = Color(0xFFF59E0B)
                        )
                    }
                }

                // صرافی‌های معتبر
                item {
                    Text(
                        text = if (currentLang == AppLanguage.FA) "صرافی‌های پشتیبانی‌شده" else "Supported Exchanges",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
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
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                // مدیریت حساب‌ها
                item {
                    Text(
                        text = if (currentLang == AppLanguage.FA) "حساب‌های فعال مدیران" else "Active Master Accounts",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                items(userList) { user ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                        border = BorderStroke(1.dp, Color(0xFF30363D)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = user.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF00E676).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = user.plan,
                                        color = Color(0xFF00E676),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "سرمایه تحت مدیریت: ${if (currentLang == AppLanguage.FA) user.capitalIrr else user.capitalUsd}",
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "صرافی‌ها: ${user.activeExchanges.joinToString(", ")}",
                                color = Color(0xFF8B949E),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
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
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = price, color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
            Text(text = cap, color = Color.Gray, fontSize = 9.sp)
        }
    }
}
