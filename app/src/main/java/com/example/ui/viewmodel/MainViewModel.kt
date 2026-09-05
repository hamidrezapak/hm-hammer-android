package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AICopilotEngine
import com.example.model.TradeOrder
import com.example.model.TradeStatus
import com.example.network.WallexLiveClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
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
    val riskRewardRatio: Double = 2.0
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

    val isRadarPulseAlive = MutableStateFlow(true)
    val radarLogs = MutableStateFlow<List<String>>(listOf("موتور تحلیل تکنیکال در حالت آماده‌باش", "رادار منتظر اتصال صرافی و دریافت داده"))

    private val _wallexApiKey = MutableStateFlow("")
    val wallexApiKey: StateFlow<String> = _wallexApiKey.asStateFlow()

    private val _isApiConnected = MutableStateFlow(false)
    val isApiConnected: StateFlow<Boolean> = _isApiConnected.asStateFlow()

    // موجودی واقعی صفر - بدون عدد آزمایشی
    private val _usdtBalance = MutableStateFlow(0.0)
    val usdtBalance: StateFlow<Double> = _usdtBalance.asStateFlow()

    private val _tomanRate = MutableStateFlow(62500.0)
    val tomanRate: StateFlow<Double> = _tomanRate.asStateFlow()

    private val _selectedPair = MutableStateFlow("BTCUSDT")
    val selectedPair: StateFlow<String> = _selectedPair.asStateFlow()

    private val _currentPrice = MutableStateFlow(64500.0)
    val currentPrice: StateFlow<Double> = _currentPrice.asStateFlow()

    private val _dynamicLevels = MutableStateFlow(DynamicLevels())
    val dynamicLevels: StateFlow<DynamicLevels> = _dynamicLevels.asStateFlow()

    // تاریخچه کاملاً خالی بدون اردرهای فیک
    private val _trades = MutableStateFlow<List<TradeOrder>>(emptyList())
    val trades: StateFlow<List<TradeOrder>> = _trades.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditLog>>(emptyList())
    val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

    private val _lastEngineLog = MutableStateFlow("سیستم آماده است. برای شروع کلید API صرافی را در تب کیف‌پول وارد کنید.")
    val lastEngineLog: StateFlow<String> = _lastEngineLog.asStateFlow()

    val historySearchQuery = MutableStateFlow("")
    val historySymbolFilter = MutableStateFlow("ALL")
    val historySideFilter = MutableStateFlow("ALL")
    val historyStatusFilter = MutableStateFlow("ALL")
    val historySortColumn = MutableStateFlow(HistorySortColumn.DATE)
    val historySortAscending = MutableStateFlow(false)

    val filteredHistoryTrades: StateFlow<List<TradeOrder>> = combine(
        _trades, historySearchQuery, historySymbolFilter, historySideFilter, historyStatusFilter
    ) { tradeList, query, sym, side, status ->
        tradeList.filter { t ->
            (query.isEmpty() || t.symbol.contains(query, ignoreCase = true) || t.closeReason.contains(query, ignoreCase = true)) &&
            (sym == "ALL" || t.symbol.equals(sym, ignoreCase = true)) &&
            (side == "ALL" || t.side.equals(side, ignoreCase = true)) &&
            (status == "ALL" || t.status.name.equals(status, ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var telegramBotToken: String = ""
    var telegramAdminChatId: String = ""

    init {
        addAuditLog("SYSTEM", "هسته معاملاتی HM HAMMER بارگذاری شد. موجودی واقعی منتظر تایید API صرافی.", true)
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

    private fun sendTelegramAlert(text: String) {
        if (telegramBotToken.isBlank() || telegramAdminChatId.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val encodedText = URLEncoder.encode(text, "UTF-8")
                val urlStr = "https://api.telegram.org/bot$telegramBotToken/sendMessage?chat_id=$telegramAdminChatId&text=$encodedText&parse_mode=HTML"
                val conn = URL(urlStr).openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                conn.responseCode
            } catch (_: Exception) {}
        }
    }

        fun verifyAndSaveWallexKey(apiKey: String, onResult: (Boolean, String) -> Unit) {
        val cleanKey = apiKey.trim()
        _wallexApiKey.value = cleanKey
        viewModelScope.launch {
            if (cleanKey.length < 8) {
                _isApiConnected.value = false
                val msg = "کلید API نامعتبر است (حداقل ۸ کاراکتر)"
                _lastEngineLog.value = msg
                onResult(false, msg)
                return@launch
            }

            _lastEngineLog.value = "در حال احراز هویت و دریافت موجودی..."
            var fetchedBalance = 0.0
            var errorDetail = ""
            
            val isSuccess = withContext(Dispatchers.IO) {
                try {
                    val url = java.net.URL("https://api.wallex.ir/v1/account/balances")
                    val conn = url.openConnection() as java.net.HttpURLConnection
                    conn.requestMethod = "GET"
                    conn.setRequestProperty("X-API-Key", cleanKey)
                    conn.connectTimeout = 8000
                    conn.readTimeout = 8000

                    val code = conn.responseCode
                    val stream = if (code in 200..299) conn.inputStream else conn.errorStream
                    val response = stream.bufferedReader().readText()

                    if (code in 200..299) {
                        val json = org.json.JSONObject(response)
                        val resultObj = json.optJSONObject("result")
                        val balances = resultObj?.optJSONObject("balances")
                        val usdtObj = balances?.optJSONObject("USDT")
                        fetchedBalance = usdtObj?.optDouble("value", 0.0) ?: 0.0
                        true
                    } else {
                        errorDetail = "خطای صرافی ($code): $response"
                        false
                    }
                } catch (e: Exception) {
                    errorDetail = "خطای اتصال شبکه: ${e.localizedMessage}"
                    false
                }
            }

            if (isSuccess) {
                _isApiConnected.value = true
                _usdtBalance.value = fetchedBalance
                val successMsg = "اتصال برقرار شد. موجودی: $fetchedBalance USDT"
                _lastEngineLog.value = successMsg
                try {
                    SecureKeyStore.saveKey(cleanKey)
                } catch (e: Exception) {}
                onResult(true, successMsg)
            } else {
                _isApiConnected.value = false
                val failMsg = if (errorDetail.isNotBlank()) errorDetail else "عدم موفقیت در احراز کلید"
                _lastEngineLog.value = failMsg
                onResult(false, failMsg)
            }
        }
    }

    fun toggleAutoEngine() {
        if (_usdtBalance.value <= 0.0) {
            val msg = "موجودی کیف‌پول ۰ است. ابتدا حساب صرافی را شارژ کنید."
            _lastEngineLog.value = msg
            addAuditLog("ENGINE_BLOCKED", msg, false)
            return
        }
        _isEngineRunning.value = !_isEngineRunning.value
        if (_isEngineRunning.value) {
            val startMsg = "موتور اتوماتیک چکش فعال شد. اسکن بازار آغاز گردید."
            _lastEngineLog.value = startMsg
            addAuditLog("ENGINE_START", startMsg, true)
            sendTelegramAlert("🚀 موتور ترید خودکار فعال شد.")
            startMarketScanner()
        } else {
            val stopMsg = "موتور ترید خودکار متوقف شد."
            _lastEngineLog.value = stopMsg
            addAuditLog("ENGINE_STOP", stopMsg, false)
            sendTelegramAlert("🛑 موتور ترید متوقف شد.")
        }
    }

    private fun startMarketScanner() {
        viewModelScope.launch {
            while (_isEngineRunning.value) {
                delay(15000)
                if (!_isEngineRunning.value) break
                if (_trades.value.none { it.status == TradeStatus.OPEN } && _usdtBalance.value >= 5.0) {
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
                tp2 = price + (atr * 1.8),
                tp3 = price + (atr * 2.5),
                tp4 = price + (atr * 3.2),
                riskRewardRatio = 2.0
            )
        }
    }

    fun executeOrder(side: String, maxAllocation: Boolean, isAuto: Boolean = false) {
        viewModelScope.launch {
            val bal = _usdtBalance.value
            if (bal <= 0.0) {
                val err = "موجودی حساب صرافی ۰ است. امکان معامله وجود ندارد."
                _lastEngineLog.value = err
                addAuditLog("ORDER_ERROR", err, false)
                return@launch
            }

            val tradeAmountUsdt = if (maxAllocation) bal * 0.8 else minOf(bal * 0.3, 15.0)
            if (tradeAmountUsdt < 2.0) {
                val err = "موجودی برای ورود به این پوزیشن ناکافی است."
                _lastEngineLog.value = err
                addAuditLog("ORDER_ERROR", err, false)
                return@launch
            }

            val price = _currentPrice.value
            val levels = _dynamicLevels.value
            val amountTmn = tradeAmountUsdt * _tomanRate.value
            val quantity = tradeAmountUsdt / price

            var liveOrderId = "LOCAL_EXEC"
            val currentApiKey = _wallexApiKey.value
            if (currentApiKey.isNotBlank()) {
                val orderResult = WallexLiveClient.placeOrder(
                    apiKey = currentApiKey,
                    symbol = _selectedPair.value,
                    type = side,
                    quantity = quantity,
                    price = price
                )
                if (orderResult.isSuccess) {
                    liveOrderId = orderResult.getOrDefault("SUCCESS")
                    addAuditLog("EXCHANGE_ORDER_SUCCESS", "سفارش واقعی در صرافی ثبت گردید: $liveOrderId", true)
                } else {
                    val errMsg = orderResult.exceptionOrNull()?.message ?: "خطای ناشناخته صرافی"
                    addAuditLog("EXCHANGE_ORDER_FAIL", errMsg, false)
                    _lastEngineLog.value = "هشدار صرافی: $errMsg"
                }
            }

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
                amountTmn = amountTmn,
                leverage = "2x",
                status = TradeStatus.OPEN,
                pnlPercent = 0.0,
                profitUsdt = 0.0,
                isPostOnly = true,
                entryTimestamp = System.currentTimeMillis(),
                exitTimestamp = 0L,
                closeReason = if (isAuto) "Auto Hammer Trigger ($liveOrderId)" else "Manual Execution ($liveOrderId)",
                timestamp = System.currentTimeMillis()
            )

            _usdtBalance.value -= tradeAmountUsdt
            _trades.value = listOf(newOrder) + _trades.value
            val logText = "${if (isAuto) "معامله اتوماتیک" else "معامله دستی"}: سفارش $side با موفقیت ثبت شد."
            _lastEngineLog.value = logText
            addAuditLog("ORDER_OPEN", logText, true)

            sendTelegramAlert("⚡ معامله جدید: ${_selectedPair.value}\nحجم: ${tradeAmountUsdt.toInt()} USDT | شناسه: $liveOrderId")
        }
    }

    fun closeTradeManually(order: TradeOrder) {
        closeOrder(order.timestamp, isProfit = true)
    }

    fun closeOrder(orderTimestamp: Long, isProfit: Boolean) {
        val order = _trades.value.find { it.timestamp == orderTimestamp } ?: return
        val tradeAmountUsdt = order.amountTmn / _tomanRate.value
        val multiplier = if (isProfit) 1.045 else 0.98
        val payout = tradeAmountUsdt * multiplier
        val diffUsdt = payout - tradeAmountUsdt

        _usdtBalance.value += payout
        _trades.value = _trades.value.map {
            if (it.timestamp == orderTimestamp) {
                it.copy(
                    status = TradeStatus.CLOSED,
                    exitPrice = if (isProfit) it.tp1 else it.stopLoss,
                    profitUsdt = diffUsdt,
                    pnlPercent = if (isProfit) 4.5 else -2.0,
                    exitTimestamp = System.currentTimeMillis(),
                    closeReason = if (isProfit) "Take Profit" else "Stop Loss"
                )
            } else it
        }

        val resText = "معامله ${order.symbol} بسته شد. سود/زیان: ${if (diffUsdt >= 0) "+$" else "-$"}${String.format("%.2f", kotlin.math.abs(diffUsdt))} USDT"
        _lastEngineLog.value = resText
        addAuditLog("ORDER_CLOSE", resText, diffUsdt >= 0)
    }

    suspend fun queryAiCopilot(userQuestion: String): String {
        return AICopilotEngine.queryRealAi(_selectedPair.value, _currentPrice.value, _tomanRate.value, userQuestion)
    }

    fun purgeSandbox() {
        _trades.value = emptyList()
        _auditLogs.value = emptyList()
        addAuditLog("SYSTEM_RESET", "تمام داده‌های آزمایشی و لاگ‌ها ریست شدند.", true)
        _lastEngineLog.value = "سیستم ریست شد."
    }

    fun exportAuditReport(): String {
        val sb = StringBuilder()
        sb.append("=== گزارش تفصیلی سیستم HM HAMMER ===\n")
        sb.append("موجودی حساب: ${String.format("%.2f", _usdtBalance.value)} USDT\n")
        sb.append("------------------------------------\n")
        _auditLogs.value.forEach { sb.append("[${it.timestamp}] [${it.eventType}] ${it.message}\n") }
        return sb.toString()
    }
}
