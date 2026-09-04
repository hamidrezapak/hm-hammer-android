package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*
import com.example.ui.theme.DarkNavyBg
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = DarkNavyBg,
                    surface = Color(0xFF161B22),
                    primary = Color(0xFF38BDF8)
                )
            ) {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val isRunning by viewModel.isEngineRunning.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .statusBarsPadding() // فاصله ایمن از بالای صفحه و آیکون‌های باتری/آنتن
    ) {
        // نوار عنوان اختصاصی با حاشیه مناسب
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF161B22))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "HM HAMMER PRO",
                color = Color(0xFF38BDF8),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Surface(
                color = if (isRunning) Color(0xFF00E676).copy(alpha = 0.2f) else Color(0xFFFF9800).copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    if (isRunning) "ENGINE ACTIVE ●" else "STANDBY ●",
                    color = if (isRunning) Color(0xFF00E676) else Color(0xFFFF9800),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // تب‌بار اسکرولی با پس‌زمینه یکدست
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0D1117))
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AppTab.values().forEach { tab ->
                val isSelected = currentTab == tab
                Button(
                    onClick = { viewModel.setTab(tab) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFF00E5FF) else Color(0xFF161B22)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        tab.titleFa,
                        color = if (isSelected) Color.Black else Color.LightGray,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // فضای نمایش محتوا
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(DarkNavyBg)
        ) {
            when (currentTab) {
                AppTab.CHART -> ChartRadarScreen(viewModel = viewModel)
                AppTab.TRADE -> TradeScreen(viewModel = viewModel)
                AppTab.AI_COPILOT -> AICopilotScreen(viewModel = viewModel)
                AppTab.HISTORY -> TransactionHistoryScreen(viewModel = viewModel)
                AppTab.WALLET -> WalletScreen(viewModel = viewModel)
                AppTab.PERFORMANCE -> PerformanceScreen(viewModel = viewModel)
                AppTab.SUBSCRIPTIONS -> SubscriptionsScreen(viewModel = viewModel)
                AppTab.HELP -> HelpGuideScreen()
                AppTab.ADMIN -> AdminScreen(viewModel = viewModel)
            }
        }
    }
}
