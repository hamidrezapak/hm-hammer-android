package com.example.ui.screens

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
    val tomanRate by viewModel.tomanRate.collectAsState()
    val formatter = remember { DecimalFormat("#,###") }

    val plans = listOf(
        SubscriptionPlan(
            id = "BRONZE",
            titleFa = "پلن برنزی (پایه)",
            titleEn = "Bronze Plan",
            basePriceUsdt = 19.0,
            badgeColor = Color(0xFFCD7F32),
            maxCapitalAllocation = "حداکثر ورود سرمایه: تا ۲۰۰ تتر",
            maxConcurrentTrades = "حداکثر ۱ پوزیشن همزمان",
            description = "تحلیل الگوی چکش روی تمام کندل‌ها + مدیریت ریسک پایه",
            hasDurationSelection = false
        ),
        SubscriptionPlan(
            id = "SILVER",
            titleFa = "پلن نقره‌ای (استاندارد)",
            titleEn = "Silver Plan",
            basePriceUsdt = 39.0,
            badgeColor = Color(0xFFC0C0C0),
            maxCapitalAllocation = "حداکثر ورود سرمایه: تا ۵۰۰ تتر",
            maxConcurrentTrades = "حداکثر ۳ پوزیشن همزمان",
            description = "اسکن تمام تایم‌فریم‌ها + سطوح دینامیک فیبوناچی و استاپ اتوماتیک",
            hasDurationSelection = false
        ),
        SubscriptionPlan(
            id = "GOLD",
            titleFa = "پلن طلایی (پرو مکس)",
            titleEn = "Gold Pro Max",
            basePriceUsdt = 79.0,
            badgeColor = Color(0xFFFFD700),
            maxCapitalAllocation = "حداکثر ورود سرمایه: تا ۱۵۰۰ تتر",
            maxConcurrentTrades = "حداکثر ۵ پوزیشن همزمان",
            description = "اجرای آنی پوزیشن‌ها روی تمام کندل‌ها + هوش مصنوعی تحلیلی زنده",
            hasDurationSelection = true
        ),
        SubscriptionPlan(
            id = "VIP_MASTER",
            titleFa = "پلن VIP مستر (نامحدود)",
            titleEn = "VIP Master",
            basePriceUsdt = 149.0,
            badgeColor = Color(0xFF00E676),
            maxCapitalAllocation = "حداکثر ورود سرمایه: بدون محدودیت حجم",
            maxConcurrentTrades = "معاملات همزمان نامحدود",
            description = "اولویت دسترسی سرور + موتور فوق‌سریع چکش + پشتیبانی اختصاصی ترید",
            hasDurationSelection = true
        )
    )

    var goldDuration by remember { mutableStateOf(PlanDuration.ONE_MONTH) }
    var vipDuration by remember { mutableStateOf(PlanDuration.ONE_MONTH) }

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

                        // انتخابگر دوره فقط برای پلن‌های طلایی و VIP
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

                        // بخش قیمت ریالی و دلاری
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "${formatter.format(finalPriceToman)} تومان",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    "معادل $finalPriceUsdt تتر (USDT)",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 11.sp
                                )
                            }

                            Button(
                                onClick = {
                                    viewModel.addAuditLog(
                                        "PLAN_SELECT",
                                        "پلن ${plan.titleFa} (${selectedDuration.titleFa}) به ارزش $finalPriceUsdt USDT انتخاب شد.",
                                        true
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = plan.badgeColor),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("انتخاب پلن", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
