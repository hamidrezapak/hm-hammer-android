package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkNavyBg
import com.example.ui.viewmodel.MainViewModel
import java.text.DecimalFormat

enum class PlanDuration(val months: Int, val titleFa: String, val discountFactor: Double) {
    ONE_MONTH(1, "۱ ماهه", 1.0),
    THREE_MONTHS(3, "۳ ماهه (۱۵٪ تخفیف)", 0.85),
    SIX_MONTHS(6, "۶ ماهه (۲۵٪ تخفیف)", 0.75)
}

data class SubscriptionPlan(
    val id: String,
    val titleFa: String,
    val titleEn: String,
    val basePriceUsdt: Double,
    val badgeColor: Color,
    val maxCapitalAllocation: String,
    val maxConcurrentTrades: String,
    val description: String,
    val hasDurationSelection: Boolean = false
)

@Composable
fun SubscriptionsScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val tomanRate by viewModel.tomanRate.collectAsState()
    val formatter = remember { DecimalFormat("#,###") }

    val plans = listOf(
        SubscriptionPlan("BRONZE", "پلن برنزی (پایه)", "Bronze Plan", 19.0, Color(0xFFCD7F32), "ورود سرمایه: تا ۲۰۰ تتر", "۱ پوزیشن همزمان", "تحلیل الگوی چکش + استراتژی کم‌ریسک", false),
        SubscriptionPlan("SILVER", "پلن نقره‌ای (استاندارد)", "Silver Plan", 39.0, Color(0xFFC0C0C0), "ورود سرمایه: تا ۵۰۰ تتر", "۳ پوزیشن همزمان", "اسکن چندتایم‌فریمه + سطوح فیبوناچی و تریلینگ استاپ", false),
        SubscriptionPlan("GOLD", "پلن طلایی (پرو مکس)", "Gold Pro Max", 79.0, Color(0xFFFFD700), "ورود سرمایه: تا ۱۵۰۰ تتر", "۵ پوزیشن همزمان", "اجرای سریع اردرها + هوش مصنوعی تحلیلی زنده", true),
        SubscriptionPlan("VIP_MASTER", "پلن VIP مستر (نامحدود)", "VIP Master", 149.0, Color(0xFF00E676), "بدون سقف سرمایه", "معاملات همزمان نامحدود", "اولویت سرور + هوش مصنوعی نامحدود + پشتیبانی اختصاصی", true)
    )

    var goldDuration by remember { mutableStateOf(PlanDuration.ONE_MONTH) }
    var vipDuration by remember { mutableStateOf(PlanDuration.ONE_MONTH) }

    var selectedPlanForPayment by remember { mutableStateOf<Triple<SubscriptionPlan, Int, Long>?>(null) }

    // دیالوگ اتصال به درگاه پرداخت / والت
    if (selectedPlanForPayment != null) {
        val (plan, usdt, toman) = selectedPlanForPayment!!
        val usdtWalletAddress = "TYDzsxdjhRoVNipmYnPWv5vWWBc5zGo6bp" // ولت دریافت تتر (TRC20)

        AlertDialog(
            onDismissRequest = { selectedPlanForPayment = null },
            containerColor = Color(0xFF161B22),
            title = { Text("پرداخت و فعال‌سازی اشتراک", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("پلن انتخابی: ${plan.titleFa}", color = plan.badgeColor, fontWeight = FontWeight.Bold)
                    Text("مبلغ قابل پرداخت: $usdt تتر (${formatter.format(toman)} تومان)", color = Color.White, fontSize = 13.sp)
                    HorizontalDivider(color = Color(0xFF30363D))
                    
                    Text("روش پرداخت را انتخاب کنید:", color = Color.Gray, fontSize = 12.sp)
                    
                    Button(
                        onClick = {
                            val zarinpalUrl = "https://zarinp.al/hmhammer?amount=$toman"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(zarinpalUrl))
                            context.startActivity(intent)
                            selectedPlanForPayment = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF238636)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("پرداخت ریالی (درگاه آنلاین بانکی)", color = Color.White, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("USDT Address", usdtWalletAddress)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "آدرس کیف‌پول USDT TRC20 کپی شد.", Toast.LENGTH_LONG).show()
                            selectedPlanForPayment = null
                        },
                        border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("کپی آدرس کیف‌پول تتر (USDT TRC20)", color = Color(0xFF38BDF8), fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedPlanForPayment = null }) {
                    Text("انصراف", color = Color.Gray)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(12.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("اشتراک‌های معاملاتی الگوریتم چکش", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("نرخ روز: ${formatter.format(tomanRate.toInt())} تومان", color = Color(0xFF38BDF8), fontSize = 11.sp)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(plans.size) { index ->
                val plan = plans[index]
                val selectedDuration = when (plan.id) {
                    "GOLD" -> goldDuration
                    "VIP_MASTER" -> vipDuration
                    else -> PlanDuration.ONE_MONTH
                }

                val finalPriceUsdt = (plan.basePriceUsdt * selectedDuration.months * selectedDuration.discountFactor).toInt()
                val finalPriceToman = (finalPriceUsdt * tomanRate).toLong()

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                    border = BorderStroke(1.dp, plan.badgeColor.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(plan.titleFa, color = plan.badgeColor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Surface(
                                color = plan.badgeColor.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    plan.titleEn,
                                    color = plan.badgeColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Text(plan.description, color = Color.LightGray, fontSize = 11.sp, lineHeight = 16.sp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(plan.maxCapitalAllocation, color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(plan.maxConcurrentTrades, color = Color.Gray, fontSize = 11.sp)
                        }

                        if (plan.hasDurationSelection) {
                            HorizontalDivider(color = Color(0xFF21262D), thickness = 1.dp)
                            Text("انتخاب دوره زمانی اشتراک:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                PlanDuration.values().forEach { duration ->
                                    val isSelected = selectedDuration == duration
                                    OutlinedButton(
                                        onClick = {
                                            if (plan.id == "GOLD") goldDuration = duration
                                            else if (plan.id == "VIP_MASTER") vipDuration = duration
                                        },
                                        modifier = Modifier.weight(1f).height(36.dp),
                                        contentPadding = PaddingValues(2.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (isSelected) plan.badgeColor.copy(alpha = 0.2f) else Color.Transparent
                                        ),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) plan.badgeColor else Color(0xFF30363D)
                                        )
                                    ) {
                                        Text(
                                            duration.titleFa,
                                            color = if (isSelected) plan.badgeColor else Color.Gray,
                                            fontSize = 9.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = Color(0xFF21262D), thickness = 1.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("${formatter.format(finalPriceToman)} تومان", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("معادل $finalPriceUsdt تتر (USDT)", color = Color(0xFF38BDF8), fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    selectedPlanForPayment = Triple(plan, finalPriceUsdt, finalPriceToman)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = plan.badgeColor),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("خرید اشتراک", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
