package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.ui.components.AppTabBar
import com.example.ui.components.HeaderPulseBar
import com.example.ui.components.LanguageOption
import com.example.ui.screens.*
import com.example.ui.theme.DarkNavyBg
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var selectedLanguage by remember { mutableStateOf(LanguageOption.FA) }
            val isRtl = selectedLanguage == LanguageOption.FA || selectedLanguage == LanguageOption.AR
            val layoutDirection = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                MainAppScreen(
                    viewModel = viewModel,
                    currentLanguage = selectedLanguage,
                    onLanguageChanged = { selectedLanguage = it }
                )
            }
        }
    }
}

@Composable
fun MainAppScreen(
    viewModel: MainViewModel,
    currentLanguage: LanguageOption = LanguageOption.FA,
    onLanguageChanged: (LanguageOption) -> Unit = {}
) {
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
        ) {
            HeaderPulseBar(
                isAlive = isPulseAlive,
                isPulseAlive = isPulseAlive,
                currentLanguage = currentLanguage,
                onLanguageSelected = onLanguageChanged
            )

            AppTabBar(
                selectedTab = currentTab,
                onTabSelected = { viewModel.setTab(it) },
                currentLanguage = currentLanguage,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )

            when (currentTab) {
                AppTab.CHART -> ChartRadarScreen(viewModel = viewModel, currentLanguage = currentLanguage)
                AppTab.TRADE -> TradeScreen(viewModel = viewModel, currentLanguage = currentLanguage)
                AppTab.HISTORY -> TransactionHistoryScreen(viewModel = viewModel, currentLanguage = currentLanguage)
                AppTab.WALLET -> WalletScreen(viewModel = viewModel, currentLanguage = currentLanguage)
                AppTab.PERFORMANCE -> PerformanceScreen(viewModel = viewModel, currentLanguage = currentLanguage)
                AppTab.SUBSCRIPTIONS -> SubscriptionsScreen(viewModel = viewModel, currentLanguage = currentLanguage)
                AppTab.HELP -> HelpGuideScreen(currentLanguage = currentLanguage)
                AppTab.ADMIN -> AdminScreen(viewModel = viewModel, currentLanguage = currentLanguage)
            }
        }
    }
}
