package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.AppTabBar
import com.example.ui.components.HeaderPulseBar
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.ChartRadarScreen
import com.example.ui.screens.HelpGuideScreen
import com.example.ui.screens.PerformanceScreen
import com.example.ui.screens.SubscriptionsScreen
import com.example.ui.screens.TradeScreen
import com.example.ui.screens.TransactionHistoryScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val isPulseAlive by viewModel.isRadarPulseAlive.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg),
        containerColor = DarkNavyBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkNavyBg)
        ) {
            // Live Status Radar Pulse Header
            HeaderPulseBar(
                isAlive = isPulseAlive,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )

            // Scrollable App Tab Bar
            AppTabBar(
                selectedTab = currentTab,
                onTabSelected = { viewModel.setTab(it) },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )

            // Active Tab Screen View
            when (currentTab) {
                AppTab.CHART -> ChartRadarScreen(viewModel = viewModel)
                AppTab.TRADE -> TradeScreen(viewModel = viewModel)
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

