package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppTab(val title: String) {
    CHART("Chart"),
    TRADE("Trade"),
    AI_COPILOT("AI Copilot"),
    HISTORY("History"),
    WALLET("Wallet"),
    PERFORMANCE("Performance"),
    SUBSCRIPTIONS("Plans"),
    HELP("Help"),
    ADMIN("Admin")
}

class MainViewModel : ViewModel() {
    private val _currentTab = MutableStateFlow(AppTab.CHART)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _isRadarPulseAlive = MutableStateFlow(true)
    val isRadarPulseAlive: StateFlow<Boolean> = _isRadarPulseAlive.asStateFlow()

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }
}
