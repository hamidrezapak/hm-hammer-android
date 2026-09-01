package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppLocale
import com.example.ui.viewmodel.AppTab

@Composable
fun AppTabBar(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier,
    currentLanguage: LanguageOption = LanguageOption.FA
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        AppTab.values().forEach { tab ->
            val isSelected = selectedTab == tab
            val tabKey = when (tab) {
                AppTab.CHART -> "tab_chart"
                AppTab.TRADE -> "tab_trade"
                AppTab.HISTORY -> "tab_history"
                AppTab.WALLET -> "tab_wallet"
                AppTab.ADMIN -> "tab_admin"
                AppTab.HELP -> "tab_help"
                AppTab.PERFORMANCE -> "tab_performance"
                AppTab.SUBSCRIPTIONS -> "tab_plans"
            }
            val title = AppLocale.t(tabKey, currentLanguage)

            Button(
                onClick = { onTabSelected(tab) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color(0xFF00E676) else Color(0xFF21262D),
                    contentColor = if (isSelected) Color.Black else Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
