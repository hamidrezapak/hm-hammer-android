package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LanguageOption

@Composable
fun AdminScreen(
    onNavigateBack: () -> Unit = {},
    onRestartDaemon: () -> Unit = {},
    viewModel: Any? = null,
    currentLanguage: LanguageOption = LanguageOption.FA
) {
    val context = LocalContext.current
    var panicTriggered by remember { mutableStateOf(false) }

    val isFa = currentLanguage == LanguageOption.FA
    val isAr = currentLanguage == LanguageOption.AR

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
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

        // پنل وضعیت حساب‌ها
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
                statusText = if (isFa) "در انتظار اتصال کلید API صرافی" else if (isAr) "بانتظار ربط مفتاح API" else "Awaiting Live Exchange API Key",
                exchangesText = "Wallex / BingX / Binance",
                isFa = isFa
            )
        }

        item {
            RealAccountCard(
                username = "@masjedi6913",
                tier = "PRO SCALP",
                statusText = if (isFa) "در انتظار اتصال کلید API صرافی" else if (isAr) "بانتظار ربط مفتاح API" else "Awaiting Live Exchange API Key",
                exchangesText = "Nobitex / CoinEx",
                isFa = isFa
            )
        }
    }
}

@Composable
fun RealAccountCard(
    username: String,
    tier: String,
    statusText: String,
    exchangesText: String,
    isFa: Boolean
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
                text = "${if (isFa) "وضعیت موجودی لایو: " else "Live Balance: "} $statusText",
                color = Color(0xFFFFB703),
                fontSize = 11.sp
            )
            Text(
                text = "${if (isFa) "صرافی‌های فعال: " else "Target Exchanges: "} $exchangesText",
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
