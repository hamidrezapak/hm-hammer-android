package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.AppTabBar
import com.example.ui.screens.*
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.HMHammerTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HMHammerTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AppTabBar(
                currentTab = currentTab,
                onTabSelected = { viewModel.setTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkNavyBg)
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.TRADE -> TradeScreen(viewModel = viewModel)
                AppTab.CHART -> ChartRadarScreen(viewModel = viewModel)
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
