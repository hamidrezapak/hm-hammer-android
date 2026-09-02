package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.TradeStatus
import com.example.model.TradingSignal
import com.example.model.UserConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// تعریف جامع تب‌ها با مقادیر پیش‌فرض
enum class AppTab(val titleFa: String = "تب", val titleEn: String = "Tab") {
    CHART("نمودار", "Chart"),
    TRADE("معامله", "Trade"),
    AI_COPILOT("دستیار هوش‌مصنوعی", "AI Copilot"),
    HISTORY("تاریخچه", "History"),
    WALLET("کیف‌پول", "Wallet"),
    PERFORMANCE("عملکرد", "Performance"),
    SUBSCRIPTIONS("پلن‌ها", "Subscriptions"),
    HELP("راهنما", "Help"),
    ADMIN("مدیریت", "Admin")
}

// تعریف ستون‌های مرتب‌سازی تاریخچه معاملات
enum class HistorySortColumn {
    ENTRY_TIME,
    EXIT_TIME,
    PNL_PERCENT,
    SYMBOL,
    SIDE,
    NET_PROFIT
}

// مدل داده‌ای سطوح تارگت و استاپ بر پایه محاسبات ATR
data class DynamicLevels(
    val entryPrice: Double = 0.0,
    val stopLoss: Double = 0.0,
    val tp1: Double = 0.0,
    val tp2: Double = 0.0,
    val tp3: Double = 0.0,
    val tp4: Double = 0.0,
    val riskRewardRatio: Double = 1.5
)

class MainViewModel : ViewModel() {
    private val _currentTab = MutableStateFlow(AppTab.CHART)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _isRadarPulseAlive = MutableStateFlow(true)
    val isRadarPulseAlive: StateFlow<Boolean> = _isRadarPulseAlive.asStateFlow()

    // متغیرهای تاریخچه و فیلترها
    private val _historySortColumn = MutableStateFlow(HistorySortColumn.ENTRY_TIME)
    val historySortColumn: StateFlow<HistorySortColumn> = _historySortColumn.asStateFlow()

    private val _historySortAscending = MutableStateFlow(false)
    val historySortAscending: StateFlow<Boolean> = _historySortAscending.asStateFlow()

    private val _historySearchQuery = MutableStateFlow("")
    val historySearchQuery: StateFlow<String> = _historySearchQuery.asStateFlow()

    private val _historySymbolFilter = MutableStateFlow<String?>(null)
    val historySymbolFilter: StateFlow<String?> = _historySymbolFilter.asStateFlow()

    private val _historySideFilter = MutableStateFlow<String?>(null)
    val historySideFilter: StateFlow<String?> = _historySideFilter.asStateFlow()

    private val _historyStatusFilter = MutableStateFlow<TradeStatus?>(null)
    val historyStatusFilter: StateFlow<TradeStatus?> = _historyStatusFilter.asStateFlow()

    // سطوح داینامیک معامله فعلی
    private val _dynamicLevels = MutableStateFlow(DynamicLevels())
    val dynamicLevels: StateFlow<DynamicLevels> = _dynamicLevels.asStateFlow()

    private val _selectedPair = MutableStateFlow("BTC/USDT")
    val selectedPair: StateFlow<String> = _selectedPair.asStateFlow()

    private val _userConfig = MutableStateFlow(UserConfig())
    val userConfig: StateFlow<UserConfig> = _userConfig.asStateFlow()

    private val _trades = MutableStateFlow<List<TradingSignal>>(emptyList())
    val trades: StateFlow<List<TradingSignal>> = _trades.asStateFlow()

    private val _radarLogs = MutableStateFlow<List<String>>(emptyList())
    val radarLogs: StateFlow<List<String>> = _radarLogs.asStateFlow()

    private val _lastEngineLog = MutableStateFlow("موتور HM HAMMER آماده اسکن بازارهای تتری است.")
    val lastEngineLog: StateFlow<String> = _lastEngineLog.asStateFlow()

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setHistorySort(column: HistorySortColumn) {
        if (_historySortColumn.value == column) {
            _historySortAscending.value = !_historySortAscending.value
        } else {
            _historySortColumn.value = column
            _historySortAscending.value = false
        }
    }

    fun setHistorySearchQuery(query: String) {
        _historySearchQuery.value = query
    }

    fun setHistorySymbolFilter(symbol: String?) {
        _historySymbolFilter.value = symbol
    }

    fun setHistorySideFilter(side: String?) {
        _historySideFilter.value = side
    }

    fun setHistoryStatusFilter(status: TradeStatus?) {
        _historyStatusFilter.value = status
    }

    fun calculateDynamicLevels(currentPrice: Double, atr: Double, side: String = "BUY") {
        if (currentPrice <= 0.0) return
        val riskDistance = if (atr > 0) atr * 1.5 else currentPrice * 0.015

        if (side == "BUY") {
            val sl = currentPrice - riskDistance
            val tp1 = currentPrice + (riskDistance * 1.0)
            val tp2 = currentPrice + (riskDistance * 1.5)
            val tp3 = currentPrice + (riskDistance * 2.0)
            val tp4 = currentPrice + (riskDistance * 3.0)
            val rr = if (riskDistance > 0) (tp2 - currentPrice) / riskDistance else 1.5
            _dynamicLevels.value = DynamicLevels(currentPrice, sl, tp1, tp2, tp3, tp4, rr)
        } else {
            val sl = currentPrice + riskDistance
            val tp1 = currentPrice - (riskDistance * 1.0)
            val tp2 = currentPrice - (riskDistance * 1.5)
            val tp3 = currentPrice - (riskDistance * 2.0)
            val tp4 = currentPrice - (riskDistance * 3.0)
            val rr = if (riskDistance > 0) (currentPrice - tp2) / riskDistance else 1.5
            _dynamicLevels.value = DynamicLevels(currentPrice, sl, tp1, tp2, tp3, tp4, rr)
        }
    }

    fun updateAutoTrade(enabled: Boolean) {
        _userConfig.value = _userConfig.value.copy(autoTradeEnabled = enabled)
    }

    fun updateTradeAmount(amount: Double) {
        _userConfig.value = _userConfig.value.copy(tradeAmountUsdt = amount)
    }

    fun updateLeverage(lev: Int) {
        _userConfig.value = _userConfig.value.copy(leverage = lev)
    }

    fun updateExchange(exchange: String) {
        _userConfig.value = _userConfig.value.copy(exchange = exchange)
    }

    fun closeTradeManually(tradeId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _trades.value = _trades.value.filterNot { it.id == tradeId }
        }
    }
}
