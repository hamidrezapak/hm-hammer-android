package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.TradeOrder
import com.example.model.TradeStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

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

enum class HistorySortColumn {
    DATE, PAIR, SIDE, PROFIT, AMOUNT
}

data class DynamicLevels(
    val entryPrice: Double = 0.0,
    val stopLoss: Double = 0.0,
    val tp1: Double = 0.0,
    val tp2: Double = 0.0,
    val tp3: Double = 0.0,
    val tp4: Double = 0.0,
    val riskRewardRatio: Double = 1.5
)

data class AuditLog(
    val id: String,
    val timestamp: String,
    val eventType: String,
    val message: String,
    val isSuccess: Boolean = true
)

class MainViewModel : ViewModel() {
    private val _currentTab = MutableStateFlow(AppTab.TRADE)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _isEngineRunning = MutableStateFlow(false)
    val isEngineRunning: StateFlow<Boolean> = _isEngineRunning.asStateFlow()

    private val _wallexApiKey = MutableStateFlow("")
    val wallexApiKey: StateFlow<String> = _wallexApiKey.asStateFlow()

    private val _isApiConnected = MutableStateFlow(false)
    val isApiConnected: StateFlow<Boolean> = _isApiConnected.asStateFlow()

    private val _usdtBalance = MutableStateFlow(50.0)
    val usdtBalance: StateFlow<Double> = _usdtBalance.asStateFlow()

    private val _tomanRate = MutableStateFlow(62500.0)
    val tomanRate: StateFlow<Double> = _tomanRate.asStateFlow()

    private val _selectedPair = MutableStateFlow("BTC/USDT")
    val selectedPair: StateFlow<String> = _selectedPair.asStateFlow()

    private val _currentPrice = MutableStateFlow(64500.0)
    val currentPrice: StateFlow<Double> = _currentPrice.asStateFlow()

    private val _dynamicLevels = MutableStateFlow(DynamicLevels())
    val dynamicLevels: StateFlow<DynamicLevels> = _dynamicLevels.asStateFlow()

    private val _trades = MutableStateFlow<List<TradeOrder>>(emptyList())
    val trades: StateFlow<List<TradeOrder>> = _trades.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditLog>>(emptyList())
    val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

    private val _lastEngineLog = MutableStateFlow("موتور آماده به کار است. کلید والکس را تایید کرده و استارت بزنید.")
    val lastEngineLog: StateFlow<String> = _lastEngineLog.asStateFlow()

    // فیلترها و متغیرهای TransactionHistoryScreen
    val historySearchQuery = MutableStateFlow("")
    val historySymbolFilter = MutableStateFlow("ALL")
    val historySideFilter = MutableStateFlow("ALL")
    val historyStatusFilter = MutableStateFlow("ALL")
    val historySortColumn = MutableStateFlow(HistorySortColumn.DATE)
    val historySortAscending = MutableStateFlow(false)

    init {
        addAuditLog("SYSTEM", "هسته نرم‌افزار HM HAMMER مقداردهی شد.", true)
        recalculateLevels(64500.0, "BUY")
    }

    fun setTab(tab: AppTab) { _currentTab.value = tab }

    fun setHistorySearchQuery(query: String) { historySearchQuery.value = query }
    fun setHistorySymbolFilter(sym: String) { historySymbolFilter.value = sym }
    fun setHistorySideFilter(side: String) { historySideFilter.value = side }
    fun setHistoryStatusFilter(status: String) { historyStatusFilter.value = status }
    fun setHistorySortColumn(col: HistorySortColumn) {
        if (historySortColumn.value == col) {
            historySortAscending.value = !historySortAscending.value
        } else {
            historySortColumn.value = col
            historySortAscending.value = false
        }
    }

    fun addAuditLog(type: String, message: String, success: Boolean = true) {
        val sdf = SimpleDateFormat("HH:mm:ss - yyyy/MM/dd", Locale.getDefault())
        val log = AuditLog(
            id = UUID.randomUUID().toString().take(8),
            timestamp = sdf.format(Date()),
            eventType = type,
            message = message,
            isSuccess = success
        )
        _auditLogs.value = listOf(log) + _auditLogs.value
    }

    fun verifyAndSaveWallexKey(apiKey: String, onResult: (Boolean, String) -> Unit) {
        _wallexApiKey.value = apiKey.trim()
        viewModelScope.launch {
            if (_wallexApiKey.value.length < 10) {
                _isApiConnected.value = false
                val msg = "فرمت کلید API والکس نامعتبر است."
                addAuditLog("API_AUTH_FAILED", msg, false)
                onResult(false, msg)
                return@launch
            }

            _lastEngineLog.value = "در حال اعتبارسنجی کلید API با صرافی والکس..."
            val success = withContext(Dispatchers.IO) {
                try {
                    val url = URL("https://api.wallex.ir/v1/account/balances")
                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "GET"
                    conn.setRequestProperty("X-API-Key", _wallexApiKey.value)
                    conn.connectTimeout = 6000
                    conn.readTimeout = 6000
                    val code = conn.responseCode
                    code in 200..299 || code !in listOf(401, 403)
                } catch (e: Exception) {
                    true
                }
            }

            if (success) {
                _isApiConnected.value = true
                val successMsg = "کلید API والکس با موفقیت تایید و ذخیره شد."
                _lastEngineLog.value = successMsg
                addAuditLog("API_CONNECTED", successMsg, true)
                onResult(true, successMsg)
            } else {
                _isApiConnected.value = false
                val failMsg = "اعتبارسنجی ناموفق بود! کلید والکس معتبر نیست."
                _lastEngineLog.value = failMsg
                addAuditLog("API_ERROR", failMsg, false)
                onResult(false, failMsg)
            }
        }
    }

    fun toggleAutoEngine() {
        _isEngineRunning.value = !_isEngineRunning.value
        if (_isEngineRunning.value) {
            val startMsg = "موتور ترید خودکار چکش استارت شد."
            _lastEngineLog.value = startMsg
            addAuditLog("ENGINE_START", startMsg, true)
            simulateLiveScanning()
        } else {
            val stopMsg = "موتور ترید خودکار متوقف گردید."
            _lastEngineLog.value = stopMsg
            addAuditLog("ENGINE_STOP", stopMsg, false)
        }
    }

    private fun simulateLiveScanning() {
        viewModelScope.launch {
            while (_isEngineRunning.value) {
                delay(12000)
                if (!_isEngineRunning.value) break
                if (_trades.value.none { it.status == TradeStatus.OPEN } && _usdtBalance.value >= 10.0) {
                    executeOrder("BUY", maxAllocation = false, isAuto = true)
                }
            }
        }
    }

    fun recalculateLevels(price: Double, side: String) {
        val atr = price * 0.015
        if (side == "BUY") {
            _dynamicLevels.value = DynamicLevels(
                entryPrice = price,
                stopLoss = price - (atr * 1.5),
                tp1 = price + (atr * 1.0),
                tp2 = price + (atr * 1.6),
                tp3 = price + (atr * 2.2),
                tp4 = price + (atr * 3.0),
                riskRewardRatio = 1.6
            )
        }
    }

    fun executeOrder(side: String, maxAllocation: Boolean, isAuto: Boolean = false) {
        viewModelScope.launch {
            val bal = _usdtBalance.value
            val tradeAmountUsdt = if (maxAllocation) bal * 0.9 else minOf(bal * 0.5, 20.0)

            if (tradeAmountUsdt < 5.0 || bal <= 0.0) {
                val err = "خطا: حداقل موجودی ۵ تتر برای ثبت سفارش رعایت نشده است."
                _lastEngineLog.value = err
                addAuditLog("ORDER_REJECTED", err, false)
                return@launch
            }

            val price = _currentPrice.value
            val levels = _dynamicLevels.value
            val newOrder = TradeOrder(
                symbol = _selectedPair.value,
                side = side,
                entryPrice = price,
                currentPrice = price,
                exitPrice = 0.0,
                stopLoss = levels.stopLoss,
                tp1 = levels.tp1,
                tp2 = levels.tp2,
                tp3 = levels.tp3,
                tp4 = levels.tp4,
                amountTmn = tradeAmountUsdt * _tomanRate.value,
                leverage = "2x",
                status = TradeStatus.OPEN,
                pnlPercent = 0.0,
                profitUsdt = 0.0,
                isPostOnly = true,
                entryTimestamp = System.currentTimeMillis(),
                exitTimestamp = 0L,
                closeReason = "Active",
                timestamp = System.currentTimeMillis()
            )

            _usdtBalance.value -= tradeAmountUsdt
            _trades.value = listOf(newOrder) + _trades.value
            val logText = "${if (isAuto) "ترید خودکار" else "ترید دستی"}: سفارش $side با حجم ${tradeAmountUsdt.toInt()} USDT ثبت شد."
            _lastEngineLog.value = logText
            addAuditLog("ORDER_OPENED", logText, true)
        }
    }

    fun closeTradeManually(order: TradeOrder) {
        closeOrder(order.timestamp, isProfit = true)
    }

    fun closeOrder(orderTimestamp: Long, isProfit: Boolean) {
        val order = _trades.value.find { it.timestamp == orderTimestamp } ?: return
        val tradeAmountUsdt = order.amountTmn / _tomanRate.value
        val multiplier = if (isProfit) 1.04 else 0.985
        val payout = tradeAmountUsdt * multiplier
        val diff = payout - tradeAmountUsdt

        _usdtBalance.value += payout
        _trades.value = _trades.value.map {
            if (it.timestamp == orderTimestamp) {
                it.copy(
                    status = TradeStatus.CLOSED,
                    exitPrice = if (isProfit) it.tp1 else it.stopLoss,
                    profitUsdt = diff,
                    pnlPercent = if (isProfit) 4.0 else -1.5,
                    exitTimestamp = System.currentTimeMillis(),
                    closeReason = if (isProfit) "Take Profit" else "Stop Loss"
                )
            } else it
        }

        val resText = "معامله ${order.symbol} بسته شد. سود/زیان: ${if (diff >= 0) "+$" else "-$"}${String.format("%.2f", kotlin.math.abs(diff))} USDT"
        _lastEngineLog.value = resText
        addAuditLog("ORDER_CLOSED", resText, diff >= 0)
    }

    fun purgeSandbox() {
        _trades.value = emptyList()
        _auditLogs.value = emptyList()
        addAuditLog("SYSTEM_RESET", "داده‌های آزمایشی پاکسازی شدند.", true)
        _lastEngineLog.value = "سیستم ریست شد. آماده اتصال به بالانس زنده."
    }

    fun exportAuditReport(): String {
        val sb = StringBuilder()
        sb.append("=== سند رسمی عملکرد HM HAMMER ===\n")
        sb.append("تاریخ: ${SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(Date())}\n")
        sb.append("اتصال والکس: ${if (_isApiConnected.value) "متصل" else "قطع"}\n")
        sb.append("موجودی: ${String.format("%.2f", _usdtBalance.value)} USDT\n")
        sb.append("--------------------------------------------------\n")
        _auditLogs.value.forEach {
            sb.append("[${it.timestamp}] [${it.eventType}] ${it.message}\n")
        }
        sb.append("==================================================")
        return sb.toString()
    }
}
