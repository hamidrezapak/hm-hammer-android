package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LanguageOption
import com.example.ui.theme.AppLocale
import com.example.ui.viewmodel.MainViewModel

@Composable
fun SubscriptionsScreen(
    viewModel: MainViewModel? = null,
    currentLanguage: LanguageOption = LanguageOption.FA
) {
    val isFa = currentLanguage == LanguageOption.FA
    val isAr = currentLanguage == LanguageOption.AR

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = if (isFa) "پلن‌های اشتراک + ۲.۵٪ کارمزد سود" else if (isAr) "باقات الاشتراك + 2.5% عمولة أرباح" else "Subscription Plans & 2.5% Profit Share",
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // ۱. پلن استاندارد (Standard)
        item {
            PlanCardItem(
                title = "STANDARD",
                price = if (isFa) "۱,۵۰۰,۰۰۰ تومان / ماهانه" else if (isAr) "$25 / شهرياً" else "$25 / Month",
                cap = if (isFa) "محدودیت سرمایه تا ۱,۰۰۰ دلار" else "Max Capital $1,000",
                desc = if (isFa) "مناسب شروع • بدون اسلیپیج • اتصال مستقیم صرافی نوبیتکس و والکس"
                else if (isAr) "مناسب للمبتدئين • بدون انزلاق سعري • ربط مباشر للمنصات"
                else "Ideal for beginners • Zero slippage • Direct API access",
                color = Color(0xFF38BDF8),
                btnText = if (isFa) "انتخاب پلن استاندارد" else if (isAr) "اختيار باقة ستاندرد" else "SELECT STANDARD"
            )
        }

        // ۲. پلن پرو (PRO)
        item {
            PlanCardItem(
                title = "PRO CLASSIC",
                price = if (isFa) "۳,۸۰۰,۰۰۰ تومان / ماهانه" else if (isAr) "$60 / شهرياً" else "$60 / Month",
                cap = if (isFa) "محدودیت سرمایه تا ۱۰,۰۰۰ دلار" else "Max Capital $10,000",
                desc = if (isFa) "مدیریت ریسک خودکار ۲٪ • اهرم تا ۵ برابر • فیلتر واگرایی RSI"
                else if (isAr) "إدارة مخاطر آلية 2% • رافعة مالية حتى 5x • فلتر RSI الذكي"
                else "2% Auto Risk Lock • Up to 5x Leverage • RSI Divergence Filter",
                color = Color(0xFF818CF8),
                btnText = if (isFa) "ارتقا به پلن پرو" else if (isAr) "ترقية إلى باقة برو" else "UPGRADE TO PRO"
            )
        }

        // ۳. پلن الیت (ELITE)
        item {
            PlanCardItem(
                title = "PRO ELITE",
                price = if (isFa) "۸,۵۰۰,۰۰۰ تومان / ماهانه" else if (isAr) "$135 / شهرياً" else "$135 / Month",
                cap = if (isFa) "سرمایه تا ۵۰,۰۰۰ دلار" else "Max Capital $50,000",
                desc = if (isFa) "ثبت تا ۵۰ اردر همزمان • حد سود پویا ATR سه‌گانه • رادار هشدار آنی"
                else if (isAr) "تنفيذ حتى 50 صفقة دفعة واحدة • أهداف ATR ثلاثية • رادار تنبيهات حي"
                else "Up to 50 batch orders • Triple ATR TP Targets • Live Radar Signals",
                color = Color(0xFFC084FC),
                btnText = if (isFa) "ارتقا به پلن الیت" else if (isAr) "ترقية إلى باقة إليت" else "UPGRADE TO ELITE"
            )
        }

        // ۴. پلن وی‌آی‌پی (VIP)
        item {
            PlanCardItem(
                title = "VIP ELITE MASTER",
                price = if (isFa) "۱۹,۰۰۰,۰۰۰ تومان / ماهانه" else if (isAr) "$300 / شهرياً" else "$300 / Month",
                cap = if (isFa) "سرمایه نامحدود • بدون سقف" else "Unlimited Capital",
                desc = if (isFa) "اولویت شماره یک پردازش • صفر درصد کارمزد پلتفرم • سرور و انجین اختصاصی"
                else if (isAr) "أولوية قصوى للتنفيذ • 0.0% عمولة المنصة • خادم ومحرك خاص 24/7"
                else "Priority #1 Order Dispatch • 0.0% Platform Commission • 24/7 Dedicated Engine",
                color = Color(0xFFF59E0B),
                btnText = if (isFa) "فعال‌سازی پلن VIP" else if (isAr) "تفعيل باقة VIP" else "ACTIVATE VIP MASTER"
            )
        }
    }
}

@Composable
fun PlanCardItem(title: String, price: String, cap: String, desc: String, color: Color, btnText: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = title, color = color, fontWeight = FontWeight.Black, fontSize = 14.sp)
                Text(text = price, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = cap, color = Color(0xFF00E676), fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = desc, color = Color.LightGray, fontSize = 11.sp, lineHeight = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = color),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth().height(38.dp)
            ) {
                Text(btnText, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
    }
}
