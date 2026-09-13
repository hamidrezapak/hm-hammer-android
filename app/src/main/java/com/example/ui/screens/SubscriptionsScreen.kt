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
import com.example.network.SubscriptionStore
import com.example.network.TelegramNotifier
import com.example.ui.theme.DarkNavyBg
import com.example.ui.viewmodel.MainViewModel
import com.example.util.ActivationCodeUtil
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

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

data class PendingPayment(
    val plan: SubscriptionPlan,
    val priceUsdt: Int,
    val priceToman: Long,
    val months: Int
)

@Composable
fun SubscriptionsScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
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

    var selectedPlanForPayment by remember { mutableStateOf<PendingPayment?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) }

    val activePlanId = remember(refreshTrigger) { SubscriptionStore.getActivePlan() }
    val pendingPlanId = remember(refreshTrigger) { SubscriptionStore.getPendingPlan() }
    val pendingTracking = remember(refreshTrigger) { SubscriptionStore.getPendingTracking() }
    var activationInput by remember { mutableStateOf("") }

    // دیالوگ اتصال به درگاه پرداخت / والت / کارت به کارت
    if (selectedPlanForPayment != null) {
        val payment = selectedPlanForPayment!!
        var showCardToCard by remember { mutableStateOf(false) }
        var trackingCodeInput by remember { mutableStateOf("") }
        val usdtWalletAddress = "TYDzsxdjhRoVNipmYnPWv5vWWBc5zGo6bp" // ولت دریافت تتر (TRC20)
        val cardNumberPlaceholder = "XXXX-XXXX-XXXX-XXXX (به نام: ---)" // TODO: شماره کارت واقعی جایگزین شود

        AlertDialog(
            onDismissRequest = { selectedPlanForPayment = null },
            containerColor = Color(0xFF161B22),
            title = { Text("پرداخت و فعال‌سازی اشتراک", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("پلن انتخابی: ${payment.plan.titleFa}", color = payment.plan.badgeColor, fontWeight = FontWeight.Bold)
                    Text("مبلغ قابل پرداخت: ${payment.priceUsdt} تتر (${formatter.format(payment.priceToman)} تومان)", color = Color.White, fontSize = 13.sp)
                    HorizontalDivider(color = Color(0xFF30363D))

                    if (!showCardToCard) {
                        Text("روش پرداخت را انتخاب کنید:", color = Color.Gray, fontSize = 12.sp)

                        Button(
                            onClick = {
                                val zarinpalUrl = "https://zarinp.al/hmhammer?amount=${payment.priceToman}"
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

                        OutlinedButton(
                            onClick = { showCardToCard = true },
                            border = BorderStroke(1.dp, Color(0xFFFFB703)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("پرداخت کارت به کارت (ثبت دستی)", color = Color(0xFFFFB703), fontSize = 12.sp)
                        }
                    } else {
                        Text("مبلغ را به شماره کارت زیر واریز کنید:", color = Color.Gray, fontSize = 12.sp)
                        Surface(
                            color = Color(0xFF0D1117),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF30363D)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                cardNumberPlaceholder,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        OutlinedTextField(
                            value = trackingCodeInput,
                            onValueChange = { trackingCodeInput = it },
                            label = { Text("کد پیگیری تراکنش") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFFB703),
                                unfocusedBorderColor = Color(0xFF30363D)
                            )
                        )
                        Text(
                            "بعد از ثبت، درخواست شما برای بررسی ارسال می‌شود و کد فعال‌سازی برایتان ارسال خواهد شد.",
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                        Button(
                            onClick = {
                                if (trackingCodeInput.isBlank()) {
                                    Toast.makeText(context, "لطفاً کد پیگیری تراکنش را وارد کنید", Toast.LENGTH_SHORT).show()
                                } else {
                                    SubscriptionStore.savePendingClaim(payment.plan.id, trackingCodeInput, payment.months)
                                    coroutineScope.launch {
                                        TelegramNotifier.sendNotification(
                                            "🔔 درخواست پرداخت کارت به کارت جدید\n" +
                                                "پلن: ${payment.plan.titleFa}\n" +
                                                "مبلغ: ${formatter.format(payment.priceToman)} تومان\n" +
                                                "کد پیگیری: $trackingCodeInput"
                                        )
                                    }
                                    Toast.makeText(context, "درخواست ثبت شد. منتظر کد فعال‌سازی بمانید.", Toast.LENGTH_LONG).show()
                                    selectedPlanForPayment = null
                                    refreshTrigger++
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB703)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("ثبت درخواست پرداخت", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
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

        // وضعیت اشتراک فعلی / درخواست در انتظار تایید
        if (activePlanId != null) {
            val expiryDate = remember(activePlanId) {
                SimpleDateFormat("yyyy/MM/dd", Locale.US).format(Date(SubscriptionStore.getActiveExpiry()))
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF102A1A)),
                border = BorderStroke(1.dp, Color(0xFF00E676)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "اشتراک فعال: ${plans.find { it.id == activePlanId }?.titleFa ?: activePlanId}",
                        color = Color(0xFF00E676),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text("معتبر تا تاریخ: $expiryDate", color = Color.LightGray, fontSize = 11.sp)
                }
            }
        } else if (pendingPlanId.isNotBlank()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2410)),
                border = BorderStroke(1.dp, Color(0xFFFFB703)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "درخواست شما برای پلن ${plans.find { it.id == pendingPlanId }?.titleFa ?: pendingPlanId} در انتظار تایید است",
                        color = Color(0xFFFFB703),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text("کد پیگیری ثبت‌شده: $pendingTracking", color = Color.Gray, fontSize = 11.sp)
                    OutlinedTextField(
                        value = activationInput,
                        onValueChange = { activationInput = it },
                        label = { Text("کد فعال‌سازی") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFFFB703),
                            unfocusedBorderColor = Color(0xFF30363D)
                        )
                    )
                    Button(
                        onClick = {
                            val expected = ActivationCodeUtil.generate(pendingTracking, pendingPlanId)
                            if (activationInput.trim().equals(expected, ignoreCase = true)) {
                                val months = SubscriptionStore.getPendingMonths()
                                val expiry = System.currentTimeMillis() + (months.toLong() * 30L * 24 * 60 * 60 * 1000)
                                SubscriptionStore.activatePlan(pendingPlanId, expiry)
                                Toast.makeText(context, "اشتراک با موفقیت فعال شد!", Toast.LENGTH_LONG).show()
                                activationInput = ""
                                refreshTrigger++
                            } else {
                                Toast.makeText(context, "کد فعال‌سازی نادرست است", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB703)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("فعال‌سازی با کد", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
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
                                    selectedPlanForPayment = PendingPayment(plan, finalPriceUsdt, finalPriceToman, selectedDuration.months)
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
