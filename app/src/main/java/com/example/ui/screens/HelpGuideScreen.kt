package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LanguageOption

@Composable
fun HelpGuideScreen(
    currentLanguage: LanguageOption = LanguageOption.FA
) {
    val isFa = currentLanguage == LanguageOption.FA
    val isAr = currentLanguage == LanguageOption.AR

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = if (isFa) "پروتکل و قوانین استراتژی موتور معاملاتی" else if (isAr) "بروتوكول واستراتيجية محرك التداول" else "ANTI-FRAGILE STRATEGY RULES (v2.0)",
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            RuleCardItem(
                num = "01",
                title = if (isFa) "الگوی هندسی کندل (چکش و پین‌بار)" else if (isAr) "هندسة الشموع (المطرقة والنجم الساقط)" else "CANDLE GEOMETRY (HAMMER & SHOOTING STAR)",
                desc = if (isFa) "سایه پایینی باید حداقل ۲ برابر طول بدنه باشد و سایه بالایی کمتر از ۰.۲۵ بدنه برای تایید ورود الزامی است."
                else if (isAr) "يجب أن يكون الظل السفلي ضعف جسم الشمعة على الأقل والظل العلوي أقل من 0.25 لتأكيد الدخول."
                else "Lower shadow >= 2.0x body, upper shadow <= 0.25x body for confirmation."
            )
        }
        item {
            RuleCardItem(
                num = "02",
                title = if (isFa) "فیلتر روند ماکرو (EMA 200)" else if (isAr) "فلتر الاتجاه العام (EMA 200)" else "MACRO TREND FILTER (EMA 200)",
                desc = if (isFa) "پوزیشن خرید فقط بالای خط EMA 200 و پوزیشن فروش فقط زیر EMA 200 مجاز است."
                else if (isAr) "صفقات الشراء تتطلب أن يكون السعر أعلى من خط EMA 200 بالكامل."
                else "Long positions require Price > EMA200; Short positions require Price < EMA200."
            )
        }
        item {
            RuleCardItem(
                num = "03",
                title = if (isFa) "حد ضرر و اهداف سود پویا بر پایه ATR" else if (isAr) "وقف الخسارة وجني الأرباح الديناميكي (ATR)" else "DYNAMIC SL & TP TARGETS (ATR BASED)",
                desc = if (isFa) "حد ضرر روی ۱.۵ برابر ATR محاسبه می‌شود و با رسیدن به هدف اول به نقطه ورود منتقل می‌گردد."
                else if (isAr) "يتم تحديد وقف الخسارة عند 1.5x ATR ونقل الوقف إلى سعر الدخول عند تحقيق الهدف الأول."
                else "Stop loss fixed at 1.5x ATR with breakeven trailing protection upon TP1 hit."
            )
        }
    }
}

@Composable
fun RuleCardItem(num: String, title: String, desc: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
        border = BorderStroke(1.dp, Color(0xFF30363D)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Text(text = num, color = Color(0xFF00E676), fontWeight = FontWeight.Black, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = desc, color = Color.LightGray, fontSize = 11.sp, lineHeight = 16.sp)
        }
    }
}
