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
        val tabs = listOf(
            AppTab.CHART to "tab_chart",
            AppTab.TRADE to "tab_trade",
            AppTab.AI_COPILOT to "tab_ai",
            AppTab.HISTORY to "tab_history",
            AppTab.WALLET to "tab_wallet",
            AppTab.PERFORMANCE to "tab_performance",
            AppTab.SUBSCRIPTIONS to "tab_plans",
            AppTab.HELP to "tab_help",
            AppTab.ADMIN to "tab_admin"
        )

        tabs.forEach { (tab, key) ->
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
