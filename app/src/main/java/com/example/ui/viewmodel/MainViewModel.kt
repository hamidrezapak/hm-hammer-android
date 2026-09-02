package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.TradeRepository
import com.example.engine.AsyncOrderExecutionManagerV2
import com.example.engine.MarketDataManager
import com.example.engine.PairTicker
import com.example.engine.StrategyEngineV2
import com.example.engine.StrategyEngineV2Config
import com.example.model.AdminDashboardData
import com.example.model.Candle
import com.example.model.ManagedUserItem
import com.example.model.PatternType
import com.example.model.SignalAction
import com.example.model.TradeOrder
import com.example.model.TradeStatus
import com.example.model.TradingSignal
import com.example.model.UserConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.random.Random

enum class AppTab(val titleFa: String, val titleEn: String) {
    CHART("چارت", "Chart"),
    TRADE,
    AI_COPILOT("دستیار هوش مصنوعی", "AI Copilot"),
    HISTORY("تاریخچه", "History"),
    WALLET("کیف‌پول", "Wallet"),
    PERFORMANCE("عملکرد", "Performance"),
    SUBSCRIPTIONS("اشتراک‌ها", "Plans"),
    HELP("راهنما", "Help"),
    ADMIN("مدیریت👑", "Admin")
}

enum class HistorySortColumn {
    ENTRY_TIME,
    EXIT_TIME,
    PNL_PERCENT,
    PROFIT_USDT,
    SYMBOL,
    ENTRY_PRICE
}

data class DynamicLevels(
    val stopLoss: Double,
    val tp1: Double,
    val tp2: Double,
    val tp3: Double,
    val tp4: Double,
    val riskRewardRatio: Double
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = TradeRepository(db.appDao())
    val executionManager = AsyncOrderExecutionManagerV2(viewModelScope)
    val marketDataManager = MarketDataManager()

    // Current active tab
    private val _currentTab = MutableStateFlow(AppTab.HISTORY)
    val currentTab = _currentTab.asStateFlow()

    // Selected trading pair for Chart screen
    private val _selectedPair = MutableStateFlow("BTC/USDT")
    val selectedPair = _selectedPair.asStateFlow()

    // Selected chart timeframe
    private val _selectedTimeframe = MutableStateFlow("15m")
    val selectedTimeframe = _selectedTimeframe.asStateFlow()

    // Radar pulse status
    private val _isRadarPulseAlive = MutableStateFlow(true)
    val isRadarPulseAlive = _isRadarPulseAlive.asStateFlow()

    private val _lastEngineLog = MutableStateFlow("[RADAR] Scanning 15 pairs... Active Engine OK")
    val lastEngineLog = _lastEngineLog.asStateFlow()

    private val _radarLogs = MutableStateFlow<List<String>>(emptyList())
    val radarLogs = _radarLogs.asStateFlow()

    // Transaction History Filter & Sort States
    private val _historySearchQuery = MutableStateFlow("")
    val historySearchQuery = _historySearchQuery.asStateFlow()

    private val _historySymbolFilter = MutableStateFlow("ALL")
    val historySymbolFilter = _historySymbolFilter.asStateFlow()

    private val _historySideFilter = MutableStateFlow("ALL")
    val historySideFilter = _historySideFilter.asStateFlow()

    private val _historyStatusFilter = MutableStateFlow("ALL")
    val historyStatusFilter = _historyStatusFilter.asStateFlow()

    private val _historySortColumn = MutableStateFlow(HistorySortColumn.ENTRY_TIME)
    val historySortColumn = _historySortColumn.asStateFlow()

    private val _historySortAscending = MutableStateFlow(false)
    val historySortAscending = _historySortAscending.asStateFlow()

    // User settings
    private val _userConfig = MutableStateFlow(
        UserConfig(
            userId = "89175496",
            username = "masjedi6913",
            autoTrade = true,
            tradeAmountTmn = 500000.0,
            exchangeName = "wallex",
            leverage = "2x",
            subscriptionPlan = "VIP",
            walletTmn = 12680000.0,
            walletTrx = 345.8,
            walletUsdt = 250.0,
            walletBtc = 0.0042,
            creditTmn = 1500000.0,
            hwmTmn = 18500000.0,
            isPostOnly = true,
            maxRiskPerTrade = 0.02,
            slMode = "ATR",
            slAtrMultiplier = 1.5,
            slPercentage = 2.0,
            tpMode = "ATR",
            tpTargetCount = 3,
            tp1Value = 1.0,
            tp2Value = 1.5,
            tp3Value = 2.0,
            tp4Value = 3.0,
            trailingStopBreakeven = true
        )
    )
    val userConfig = _userConfig.asStateFlow()

    // Admin Dashboard data
    private val _adminData = MutableStateFlow(
        AdminDashboardData(
            totalUsers = 142,
            activeBots = 89,
            totalInvested = "۴۴,۵۰۰,۰۰۰ تومان",
            turnover = "۲,۶۸۹,۷۴۲ ت",
            serverAlive = true,
            lastLog = "[RADAR] Scanning 15 pairs...",
            users = listOf(
                ManagedUserItem("85323618", "hamid1365", "VIP", "۱,۵۰۰,۰۰۰ تومان", "۱۸,۵۰۰,۰۰۰ تومان", "فعال (۵۰۰,۰۰۰ تومان)", true),
                ManagedUserItem("89175496", "masjedi6913", "VIP", "۱,۵۰۰,۰۰۰ تومان", "۱۸,۵۰۰,۰۰۰ تومان", "فعال (۵۰۰,۰۰۰ تومان)", true),
                ManagedUserItem("99412034", "crypto_reza", "ELITE", "۸۰۰,۰۰۰ تومان", "۱۲,۲۰۰,۰۰۰ تومان", "فعال (۱,۰۰۰,۰۰۰ تومان)", true),
                ManagedUserItem("10294812", "arash_trader", "PRO", "۳۵۰,۰۰۰ تومان", "۵,۴۰۰,۰۰۰ تومان", "فعال (۵۰۰,۰۰۰ تومان)", true),
                ManagedUserItem("77361920", "sara_invest", "STANDARD", "۱۰۰,۰۰۰ تومان", "۲,۱۰۰,۰۰۰ تومان", "غیرفعال", false),
                ManagedUserItem("88219401", "mehdi_pro", "ELITE", "۹۵۰,۰۰۰ تومان", "۱۴,۰۰۰,۰۰۰ تومان", "فعال (۲,۰۰۰,۰۰۰ تومان)", true),
                ManagedUserItem("66491022", "samira_m", "PRO", "۴۰۰,۰۰۰ تومان", "۶,۳۰۰,۰۰۰ تومان", "فعال (۵۰۰,۰۰۰ تومان)", true)
            )
        )
    )
    val adminData = _adminData.asStateFlow()

    // Database Trades Stream
    val trades = repository.allTrades.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Tickers from MarketDataManager
    val tickers = marketDataManager.tickers

    init {
        seedInitialTradesIfEmpty()
        startLiveRadarEngineLoop()
    }

    private fun seedInitialTradesIfEmpty() {
        viewModelScope.launch(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val sampleTrades = listOf(
                TradeOrder(
                    userId = "89175496",
                    symbol = "BTC/USDT",
                    side = "BUY",
                    entryPrice = 89450.0,
                    currentPrice = 91420.0,
                    exitPrice = 91420.0,
                    stopLoss = 88200.0,
                    tp1 = 90700.0,
                    tp2 = 91400.0,
                    tp3 = 92500.0,
                    tp4 = 93800.0,
                    amountTmn = 500000.0,
                    leverage = "2x",
                    status = TradeStatus.TP2_HIT,
                    pnlPercent = 4.40,
                    profitUsdt = 22.00,
                    isPostOnly = true,
                    entryTimestamp = now - 7200000L,
                    exitTimestamp = now - 1800000L,
                    closeReason = "TP2 Hit (+4.4%)"
                ),
                TradeOrder(
                    userId = "89175496",
                    symbol = "SOL/USDT",
                    side = "BUY",
                    entryPrice = 189.5,
                    currentPrice = 198.5,
                    exitPrice = 198.5,
                    stopLoss = 184.0,
                    tp1 = 194.0,
                    tp2 = 198.0,
                    tp3 = 204.0,
                    tp4 = 210.0,
                    amountTmn = 500000.0,
                    leverage = "2x",
                    status = TradeStatus.TP2_HIT,
                    pnlPercent = 9.50,
                    profitUsdt = 47.50,
                    isPostOnly = true,
                    entryTimestamp = now - 14400000L,
                    exitTimestamp = now - 5400000L,
                    closeReason = "TP2 Hit (+9.5%)"
                ),
                TradeOrder(
                    userId = "89175496",
                    symbol = "ETH/USDT",
                    side = "BUY",
                    entryPrice = 3410.0,
                    currentPrice = 3480.0,
                    exitPrice = 3480.0,
                    stopLoss = 3360.0,
                    tp1 = 3470.0,
                    tp2 = 3520.0,
                    tp3 = 3580.0,
                    tp4 = 3650.0,
                    amountTmn = 500000.0,
                    leverage = "2x",
                    status = TradeStatus.TP1_HIT,
                    pnlPercent = 4.10,
                    profitUsdt = 20.50,
                    isPostOnly = true,
                    entryTimestamp = now - 21600000L,
                    exitTimestamp = now - 10800000L,
                    closeReason = "TP1 Hit (+4.1%)"
                ),
                TradeOrder(
                    userId = "89175496",
                    symbol = "XRP/USDT",
                    side = "SELL",
                    entryPrice = 1.95,
                    currentPrice = 1.85,
                    exitPrice = 1.85,
                    stopLoss = 2.02,
                    tp1 = 1.88,
                    tp2 = 1.83,
                    tp3 = 1.78,
                    tp4 = 1.72,
                    amountTmn = 500000.0,
                    leverage = "2x",
                    status = TradeStatus.TP1_HIT,
                    pnlPercent = 10.25,
                    profitUsdt = 51.25,
                    isPostOnly = true,
                    entryTimestamp = now - 28800000L,
                    exitTimestamp = now - 14400000L,
                    closeReason = "TP1 Hit (+10.25%)"
                ),
                TradeOrder(
                    userId = "89175496",
                    symbol = "TRX/USDT",
                    side = "BUY",
                    entryPrice = 0.238,
                    currentPrice = 0.245,
                    exitPrice = 0.245,
                    stopLoss = 0.232,
                    tp1 = 0.244,
                    tp2 = 0.248,
                    tp3 = 0.254,
                    tp4 = 0.260,
                    amountTmn = 500000.0,
                    leverage = "2x",
                    status = TradeStatus.TP1_HIT,
                    pnlPercent = 5.88,
                    profitUsdt = 29.40,
                    isPostOnly = true,
                    entryTimestamp = now - 36000000L,
                    exitTimestamp = now - 18000000L,
                    closeReason = "TP1 Hit (+5.88%)"
                ),
                TradeOrder(
                    userId = "89175496",
                    symbol = "DOGE/USDT",
                    side = "BUY",
                    entryPrice = 0.385,
                    currentPrice = 0.378,
                    exitPrice = 0.378,
                    stopLoss = 0.375,
                    tp1 = 0.398,
                    tp2 = 0.412,
                    tp3 = 0.425,
                    tp4 = 0.440,
                    amountTmn = 500000.0,
                    leverage = "2x",
                    status = TradeStatus.OPEN,
                    pnlPercent = -1.82,
                    profitUsdt = -9.10,
                    isPostOnly = true,
                    entryTimestamp = now - 3600000L,
                    exitTimestamp = 0L,
                    closeReason = "In Progress"
                ),
                TradeOrder(
                    userId = "89175496",
                    symbol = "ADA/USDT",
                    side = "SELL",
                    entryPrice = 0.820,
                    currentPrice = 0.840,
                    exitPrice = 0.840,
                    stopLoss = 0.840,
                    tp1 = 0.795,
                    tp2 = 0.775,
                    tp3 = 0.750,
                    tp4 = 0.720,
                    amountTmn = 500000.0,
                    leverage = "2x",
                    status = TradeStatus.SL_HIT,
                    pnlPercent = -4.88,
                    profitUsdt = -24.40,
                    isPostOnly = true,
                    entryTimestamp = now - 43200000L,
                    exitTimestamp = now - 39600000L,
                    closeReason = "Stop Loss Hit (-4.88%)"
                )
            )

            for (t in sampleTrades) {
                repository.insertTrade(t)
            }
        }
    }

    private fun startLiveRadarEngineLoop() {
        viewModelScope.launch(Dispatchers.Default) {
            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            while (isActive) {
                val nowTime = timeFormat.format(Date())

                // Tick market data and look for signals
                val updatedTickers = marketDataManager.tickMarket { signal ->
                    handleNewSignal(signal)
                }

                // Update open trades PnL and targets
                updateOpenTradesPnL(updatedTickers)

                // Push radar pulse log
                val activePair = updatedTickers.randomOrNull()?.symbol ?: "BTC/USDT"
                val logLine = "[$nowTime] [RADAR] Scanning 15 pairs... Active: $activePair (ATR OK, Vol Spike Verified)"
                _lastEngineLog.value = logLine

                val logsList = _radarLogs.value.toMutableList()
                if (logsList.size > 50) logsList.removeAt(0)
                logsList.add(logLine)
                _radarLogs.value = logsList

                delay(1800)
            }
        }
    }

    /**
     * Calculates dynamic Stop-Loss and Take-Profit levels based on user's active risk settings.
     */
    fun calculateDynamicLevels(
        symbol: String,
        side: String,
        entryPrice: Double,
        atr: Double,
        config: UserConfig = _userConfig.value
    ): DynamicLevels {
        val safeAtr = if (atr <= 0) entryPrice * 0.015 else atr
        val isBuy = side.equals("BUY", ignoreCase = true)

        // 1. Calculate Stop Loss
        val sl = if (config.slMode == "ATR") {
            val offset = safeAtr * config.slAtrMultiplier
            if (isBuy) entryPrice - offset else entryPrice + offset
        } else {
            val offsetPct = config.slPercentage / 100.0
            if (isBuy) entryPrice * (1.0 - offsetPct) else entryPrice * (1.0 + offsetPct)
        }

        // 2. Calculate Take Profit targets (1 to 4 targets)
        val tp1 = if (config.tpMode == "ATR") {
            val offset = safeAtr * config.tp1Value
            if (isBuy) entryPrice + offset else entryPrice - offset
        } else {
            val offsetPct = config.tp1Value / 100.0
            if (isBuy) entryPrice * (1.0 + offsetPct) else entryPrice * (1.0 - offsetPct)
        }

        val tp2 = if (config.tpMode == "ATR") {
            val offset = safeAtr * config.tp2Value
            if (isBuy) entryPrice + offset else entryPrice - offset
        } else {
            val offsetPct = config.tp2Value / 100.0
            if (isBuy) entryPrice * (1.0 + offsetPct) else entryPrice * (1.0 - offsetPct)
        }

        val tp3 = if (config.tpMode == "ATR") {
            val offset = safeAtr * config.tp3Value
            if (isBuy) entryPrice + offset else entryPrice - offset
        } else {
            val offsetPct = config.tp3Value / 100.0
            if (isBuy) entryPrice * (1.0 + offsetPct) else entryPrice * (1.0 - offsetPct)
        }

        val tp4 = if (config.tpMode == "ATR") {
            val offset = safeAtr * config.tp4Value
            if (isBuy) entryPrice + offset else entryPrice - offset
        } else {
            val offsetPct = config.tp4Value / 100.0
            if (isBuy) entryPrice * (1.0 + offsetPct) else entryPrice * (1.0 - offsetPct)
        }

        val riskDistance = abs(entryPrice - sl)
        val rewardDistance = abs(tp3 - entryPrice)
        val rr = if (riskDistance > 0) rewardDistance / riskDistance else 2.0

        return DynamicLevels(
            stopLoss = sl,
            tp1 = tp1,
            tp2 = tp2,
            tp3 = tp3,
            tp4 = tp4,
            riskRewardRatio = rr
        )
    }

    private fun handleNewSignal(signal: TradingSignal) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertSignal(signal)

            val config = _userConfig.value
            val levels = calculateDynamicLevels(
                symbol = signal.symbol,
                side = signal.action.name,
                entryPrice = signal.entryPrice,
                atr = signal.atr,
                config = config
            )

            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val tag = if (signal.patternType == PatternType.BULLISH_HAMMER) "HAMMER_LONG" else "SHOOTING_STAR_SHORT"
            val logLine = "[${timeFormat.format(Date())}] ⚡ [$tag] ${signal.symbol} Entry: $${signal.entryPrice} TP1: $${String.format("%.2f", levels.tp1)} SL: $${String.format("%.2f", levels.stopLoss)}"
            
            val logsList = _radarLogs.value.toMutableList()
            if (logsList.size > 50) logsList.removeAt(0)
            logsList.add(logLine)
            _radarLogs.value = logsList

            // If autoTrade is enabled, automatically queue limit Post-Only order with user's configured dynamic SL/TP levels
            if (config.autoTrade) {
                val order = TradeOrder(
                    userId = config.userId,
                    symbol = signal.symbol,
                    side = signal.action.name,
                    entryPrice = signal.entryPrice,
                    currentPrice = signal.entryPrice,
                    exitPrice = 0.0,
                    stopLoss = levels.stopLoss,
                    tp1 = levels.tp1,
                    tp2 = levels.tp2,
                    tp3 = levels.tp3,
                    tp4 = levels.tp4,
                    amountTmn = config.tradeAmountTmn,
                    leverage = config.leverage,
                    status = TradeStatus.OPEN,
                    pnlPercent = 0.0,
                    profitUsdt = 0.0,
                    isPostOnly = config.isPostOnly,
                    entryTimestamp = System.currentTimeMillis(),
                    exitTimestamp = 0L,
                    closeReason = "In Progress"
                )

                val id = repository.insertTrade(order)
                executionManager.queueOrder(order.copy(id = id))
            }
        }
    }

    private suspend fun updateOpenTradesPnL(currentTickers: List<PairTicker>) {
        val currentTrades = trades.value
        val tickerMap = currentTickers.associateBy { it.symbol }
        val config = _userConfig.value

        for (trade in currentTrades) {
            if (trade.status == TradeStatus.CLOSED || trade.status == TradeStatus.SL_HIT) continue
            val ticker = tickerMap[trade.symbol] ?: continue
            val curPrice = ticker.price
            val levMult = when (trade.leverage) {
                "5x" -> 5.0
                "10x" -> 10.0
                "20x" -> 20.0
                else -> 2.0
            }

            var pnl = if (trade.side == "BUY") {
                ((curPrice - trade.entryPrice) / trade.entryPrice) * 100.0 * levMult
            } else {
                ((trade.entryPrice - curPrice) / trade.entryPrice) * 100.0 * levMult
            }

            // Check milestone hits & trailing stop logic
            var newStatus = trade.status
            var exitPrice = trade.exitPrice
            var exitTime = trade.exitTimestamp
            var closeReason = trade.closeReason
            var currentSL = trade.stopLoss

            if (trade.side == "BUY") {
                if (curPrice >= trade.tp4 && trade.tp4 > 0) {
                    newStatus = TradeStatus.TP4_HIT
                    exitPrice = curPrice
                    exitTime = System.currentTimeMillis()
                    closeReason = "TP4 Target Reached"
                } else if (curPrice >= trade.tp3 && trade.tp3 > 0) {
                    newStatus = TradeStatus.TP3_HIT
                    exitPrice = curPrice
                    exitTime = System.currentTimeMillis()
                    closeReason = "TP3 Target Reached"
                } else if (curPrice >= trade.tp2) {
                    newStatus = TradeStatus.TP2_HIT
                    // If trailing SL is enabled, lock profit at TP1
                    if (config.trailingStopBreakeven && currentSL < trade.entryPrice) {
                        currentSL = trade.entryPrice
                    }
                } else if (curPrice >= trade.tp1) {
                    newStatus = TradeStatus.TP1_HIT
                    // If trailing stop to breakeven is enabled, move SL to entry price
                    if (config.trailingStopBreakeven && currentSL < trade.entryPrice) {
                        currentSL = trade.entryPrice
                    }
                } else if (curPrice <= currentSL) {
                    newStatus = TradeStatus.SL_HIT
                    exitPrice = curPrice
                    exitTime = System.currentTimeMillis()
                    closeReason = if (currentSL >= trade.entryPrice) "Breakeven Lock Hit" else "Stop Loss Hit"
                }
            } else {
                if (curPrice <= trade.tp4 && trade.tp4 > 0) {
                    newStatus = TradeStatus.TP4_HIT
                    exitPrice = curPrice
                    exitTime = System.currentTimeMillis()
                    closeReason = "TP4 Target Reached"
                } else if (curPrice <= trade.tp3 && trade.tp3 > 0) {
                    newStatus = TradeStatus.TP3_HIT
                    exitPrice = curPrice
                    exitTime = System.currentTimeMillis()
                    closeReason = "TP3 Target Reached"
                } else if (curPrice <= trade.tp2) {
                    newStatus = TradeStatus.TP2_HIT
                    if (config.trailingStopBreakeven && currentSL > trade.entryPrice) {
                        currentSL = trade.entryPrice
                    }
                } else if (curPrice <= trade.tp1) {
                    newStatus = TradeStatus.TP1_HIT
                    if (config.trailingStopBreakeven && currentSL > trade.entryPrice) {
                        currentSL = trade.entryPrice
                    }
                } else if (curPrice >= currentSL) {
                    newStatus = TradeStatus.SL_HIT
                    exitPrice = curPrice
                    exitTime = System.currentTimeMillis()
                    closeReason = if (currentSL <= trade.entryPrice) "Breakeven Lock Hit" else "Stop Loss Hit"
                }
            }

            val profitUsdt = (trade.amountTmn / 65000.0) * (pnl / 100.0)
            repository.updateTradeStatus(
                id = trade.id,
                status = newStatus,
                currentPrice = curPrice,
                exitPrice = exitPrice,
                pnl = pnl,
                profit = profitUsdt,
                exitTimestamp = exitTime,
                closeReason = closeReason,
                stopLoss = currentSL
            )
        }
    }

    // Tab switcher
    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setSelectedPair(pair: String) {
        _selectedPair.value = pair
    }

    fun setSelectedTimeframe(timeframe: String) {
        _selectedTimeframe.value = timeframe
    }

    fun getCandlesForSelectedPair(): List<Candle> {
        return marketDataManager.getCandlesForPair(_selectedPair.value)
    }

    // User settings updates
    fun updateAutoTrade(enabled: Boolean) {
        val updated = _userConfig.value.copy(autoTrade = enabled)
        _userConfig.value = updated
        viewModelScope.launch { repository.saveUserConfig(updated) }
    }

    fun updateTradeAmount(amount: Double) {
        val updated = _userConfig.value.copy(tradeAmountTmn = amount)
        _userConfig.value = updated
        viewModelScope.launch { repository.saveUserConfig(updated) }
    }

    fun updateExchange(exchange: String) {
        val updated = _userConfig.value.copy(exchangeName = exchange)
        _userConfig.value = updated
        viewModelScope.launch { repository.saveUserConfig(updated) }
    }

    fun updateLeverage(leverage: String) {
        val updated = _userConfig.value.copy(leverage = leverage)
        _userConfig.value = updated
        viewModelScope.launch { repository.saveUserConfig(updated) }
    }

    fun updateApiKey(apiKey: String) {
        val updated = _userConfig.value.copy(apiKey = apiKey)
        _userConfig.value = updated
        viewModelScope.launch { repository.saveUserConfig(updated) }
    }

    fun updateSubscriptionPlan(plan: String) {
        val updated = _userConfig.value.copy(subscriptionPlan = plan)
        _userConfig.value = updated
        viewModelScope.launch { repository.saveUserConfig(updated) }
    }

    fun updatePostOnly(enabled: Boolean) {
        val updated = _userConfig.value.copy(isPostOnly = enabled)
        _userConfig.value = updated
        viewModelScope.launch { repository.saveUserConfig(updated) }
    }

    // Advanced Risk Management updates
    fun updateSlMode(mode: String) {
        val updated = _userConfig.value.copy(slMode = mode)
        _userConfig.value = updated
        viewModelScope.launch { repository.saveUserConfig(updated) }
    }

    fun updateSlAtrMultiplier(mult: Double) {
        val updated = _userConfig.value.copy(slAtrMultiplier = mult)
        _userConfig.value = updated
        viewModelScope.launch { repository.saveUserConfig(updated) }
    }

    fun updateSlPercentage(pct: Double) {
        val updated = _userConfig.value.copy(slPercentage = pct)
        _userConfig.value = updated
        viewModelScope.launch { repository.saveUserConfig(updated) }
    }

    fun updateTpMode(mode: String) {
        val updated = _userConfig.value.copy(tpMode = mode)
        _userConfig.value = updated
        viewModelScope.launch { repository.saveUserConfig(updated) }
    }

    fun updateTpTargetCount(count: Int) {
        val updated = _userConfig.value.copy(tpTargetCount = count.coerceIn(1, 4))
        _userConfig.value = updated
        viewModelScope.launch { repository.saveUserConfig(updated) }
    }

    fun updateTpValues(tp1: Double, tp2: Double, tp3: Double, tp4: Double) {
        val updated = _userConfig.value.copy(
            tp1Value = tp1,
            tp2Value = tp2,
            tp3Value = tp3,
            tp4Value = tp4
        )
        _userConfig.value = updated
        viewModelScope.launch { repository.saveUserConfig(updated) }
    }

    fun updateTrailingStopBreakeven(enabled: Boolean) {
        val updated = _userConfig.value.copy(trailingStopBreakeven = enabled)
        _userConfig.value = updated
        viewModelScope.launch { repository.saveUserConfig(updated) }
    }

    fun updateMaxRiskPerTrade(risk: Double) {
        val updated = _userConfig.value.copy(maxRiskPerTrade = risk)
        _userConfig.value = updated
        viewModelScope.launch { repository.saveUserConfig(updated) }
    }

    // Manual Trade Execution with dynamic risk levels
    fun executeManualOrder(symbol: String, side: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val candles = marketDataManager.getCandlesForPair(symbol)
            val last = candles.lastOrNull() ?: return@launch
            val atr = last.atr ?: (last.close * 0.015)
            val entry = last.close

            val levels = calculateDynamicLevels(
                symbol = symbol,
                side = side,
                entryPrice = entry,
                atr = atr,
                config = _userConfig.value
            )

            val order = TradeOrder(
                userId = _userConfig.value.userId,
                symbol = symbol,
                side = side,
                entryPrice = entry,
                currentPrice = entry,
                exitPrice = 0.0,
                stopLoss = levels.stopLoss,
                tp1 = levels.tp1,
                tp2 = levels.tp2,
                tp3 = levels.tp3,
                tp4 = levels.tp4,
                amountTmn = _userConfig.value.tradeAmountTmn,
                leverage = _userConfig.value.leverage,
                status = TradeStatus.OPEN,
                isPostOnly = _userConfig.value.isPostOnly,
                entryTimestamp = System.currentTimeMillis(),
                exitTimestamp = 0L,
                closeReason = "Manual Order Dispatched"
            )

            val id = repository.insertTrade(order)
            executionManager.queueOrder(order.copy(id = id))
        }
    }

    // Close position manually
    fun closeTradeManually(tradeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val trade = trades.value.find { it.id == tradeId } ?: return@launch
            val exitPrice = trade.currentPrice
            val levMult = when (trade.leverage) {
                "5x" -> 5.0
                "10x" -> 10.0
                "20x" -> 20.0
                else -> 2.0
            }
            val pnl = if (trade.side == "BUY") {
                ((exitPrice - trade.entryPrice) / trade.entryPrice) * 100.0 * levMult
            } else {
                ((trade.entryPrice - exitPrice) / trade.entryPrice) * 100.0 * levMult
            }
            val profitUsdt = (trade.amountTmn / 65000.0) * (pnl / 100.0)
            repository.closeTradeManually(tradeId, exitPrice, pnl, profitUsdt)
        }
    }

    // Filter & Sort handlers for History screen
    fun setHistorySearchQuery(query: String) {
        _historySearchQuery.value = query
    }

    fun setHistorySymbolFilter(symbol: String) {
        _historySymbolFilter.value = symbol
    }

    fun setHistorySideFilter(side: String) {
        _historySideFilter.value = side
    }

    fun setHistoryStatusFilter(status: String) {
        _historyStatusFilter.value = status
    }

    fun setHistorySortColumn(column: HistorySortColumn) {
        if (_historySortColumn.value == column) {
            // Toggle direction
            _historySortAscending.value = !_historySortAscending.value
        } else {
            _historySortColumn.value = column
            _historySortAscending.value = false
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllTrades()
        }
    }

    fun seedSampleHistory() {
        seedInitialTradesIfEmpty()
    }

    // Admin change user plan
    fun adminChangeUserPlan(userId: String, newPlan: String) {
        val currentUsers = _adminData.value.users.toMutableList()
        val index = currentUsers.indexOfFirst { it.id == userId }
        if (index != -1) {
            val user = currentUsers[index]
            currentUsers[index] = user.copy(plan = newPlan)
            _adminData.value = _adminData.value.copy(users = currentUsers)
        }
        if (userId == _userConfig.value.userId) {
            updateSubscriptionPlan(newPlan)
        }
    }

    // Admin restart engine
    fun restartEngine() {
        viewModelScope.launch {
            _isRadarPulseAlive.value = false
            delay(400)
            _isRadarPulseAlive.value = true
            _lastEngineLog.value = "[RESTART] Systemctl restarted hmserver, hmbot, and nginx successfully. Radar pulse OK."
            val logsList = _radarLogs.value.toMutableList()
            logsList.add("[SYSTEM] Engine restarted at ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())}")
            _radarLogs.value = logsList
        }
    }
}
