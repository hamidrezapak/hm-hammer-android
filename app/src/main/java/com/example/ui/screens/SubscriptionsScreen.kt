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
    viewModel: MainViewModel,
    currentLanguage: LanguageOption = LanguageOption.FA
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = AppLocale.t("plans_title", currentLanguage),
                color = Color.Gray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            PlanDetailCard(
                title = "VIP ELITE",
                price = if (currentLanguage == LanguageOption.FA) "۱۹,۰۰۰,۰۰۰ تومان / ماهانه" else "$300 / Month",
                desc = AppLocale.t("plan_vip_desc", currentLanguage),
                color = Color(0xFFF59E0B),
                btnText = AppLocale.t("upgrade_btn", currentLanguage)
            )
        }
        item {
            PlanDetailCard(
                title = "PRO SCALP",
                price = if (currentLanguage == LanguageOption.FA) "۳,۸۰۰,۰۰۰ تومان / ماهانه" else "$60 / Month",
                desc = AppLocale.t("plan_pro_desc", currentLanguage),
                color = Color(0xFF818CF8),
                btnText = AppLocale.t("upgrade_btn", currentLanguage)
            )
        }
    }
}

@Composable
fun PlanDetailCard(title: String, price: String, desc: String, color: Color, btnText: String) {
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
            Spacer(modifier = Modifier.height(8.dp))
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
