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
import com.example.ui.theme.AppLocale

@Composable
fun HelpGuideScreen(
    currentLanguage: LanguageOption = LanguageOption.FA
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = AppLocale.t("help_title", currentLanguage),
                color = Color.Gray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            RuleCard(
                num = "01",
                title = AppLocale.t("rule_1_title", currentLanguage),
                desc = AppLocale.t("rule_1_desc", currentLanguage)
            )
        }
        item {
            RuleCard(
                num = "02",
                title = AppLocale.t("rule_2_title", currentLanguage),
                desc = AppLocale.t("rule_2_desc", currentLanguage)
            )
        }
        item {
            RuleCard(
                num = "03",
                title = AppLocale.t("rule_3_title", currentLanguage),
                desc = AppLocale.t("rule_3_desc", currentLanguage)
            )
        }
    }
}

@Composable
fun RuleCard(num: String, title: String, desc: String) {
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
