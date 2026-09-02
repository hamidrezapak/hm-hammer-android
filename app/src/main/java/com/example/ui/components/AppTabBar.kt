package com.example.ui.components

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
    currentLanguage: LanguageOption = LanguageOption.FA,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val tabs: List<Pair<AppTab, String>> = listOf(
            Pair(AppTab.CHART, "tab_chart"),
            Pair(AppTab.TRADE, "tab_trade"),
            Pair(AppTab.AI_COPILOT, "tab_ai"),
            Pair(AppTab.HISTORY, "tab_history"),
            Pair(AppTab.WALLET, "tab_wallet"),
            Pair(AppTab.PERFORMANCE, "tab_performance"),
            Pair(AppTab.SUBSCRIPTIONS, "tab_plans"),
            Pair(AppTab.HELP, "tab_help"),
            Pair(AppTab.ADMIN, "tab_admin")
        )

        for (item in tabs) {
            val tab = item.first
            val key = item.second
            val isSelected = selectedTab == tab
            Button(
                onClick = { onTabSelected(tab) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color(0xFF00E676) else Color(0xFF21262D)
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text(
                    text = AppLocale.t(key, currentLanguage),
                    color = if (isSelected) Color.Black else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}
