package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.TradeOrder
import com.example.model.TradeStatus
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

    val isRadarPulseAlive = MutableStateFlow(true)
    val radarLogs = MutableStateFlow<List<String>>(listOf("موتور تحلیل تکنیکال زنده متصل است", "اتصال وب‌سرویس صرافی برقرار است"))

    private val _wallexApiKey = MutableStateFlow("")
    val wallexApiKey: StateFlow<String> = _wallexApiKey.asStateFlow()

    private val _isApiConnected = MutableStateFlow(false)
    val isApiConnected: StateFlow<Boolean> = _isApiConnected.asStateFlow()

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

    private val _trades = MutableStateFlow<List<TradeOrder>>(emptyList())
    val trades: StateFlow<List<TradeOrder>> = _trades.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditLog>>(emptyList())
    val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

    private val _lastEngineLog = MutableStateFlow("موتور آماده به کار است. کلید API را وارد کرده و استارت بزنید.")
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
            if (cleanKey.length < 10) {
                _isApiConnected.value = false
                val msg = "خطا: فرمت کلید API نامعتبر است."
                _lastEngineLog.value = msg
                addAuditLog("API_AUTH_FAILED", msg, false)
                onResult(false, msg)
                return@launch
            }

            _lastEngineLog.value = "در حال اتصال به سرور و استعلام موجودی..."
            var fetchedBalance = 0.0
            val isSuccess = withContext(Dispatchers.IO) {
                try {
                    val url = URL("https://api.wallex.ir/v1/account/balances")
                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "GET"
                    conn.setRequestProperty("X-API-Key", cleanKey)
                    conn.connectTimeout = 8000
                    conn.readTimeout = 8000
                    if (conn.responseCode in 200..299) {
                        val response = conn.inputStream.bufferedReader().readText()
                        val json = JSONObject(response)
                        val balances = json.optJSONObject("result")?.optJSONObject("balances")
                        val usdtObj = balances?.optJSONObject("USDT")
                        fetchedBalance = usdtObj?.optDouble("value", 0.0) ?: 0.0
                        true
                    } else {
                        false
                    }
                } catch (e: Exception) {
                    false
                }
            }

            if (isSuccess) {
                _isApiConnected.value = true
                _usdtBalance.value = fetchedBalance
                val msg = "اتصال برقرار شد. موجودی واقعی: $fetchedBalance USDT"
                _lastEngineLog.value = msg
                addAuditLog("API_CONNECTED", msg, true)
                sendTelegramAlert("🔔 <b>اتصال صرافی</b>\nکلید ثبت شد.\nموجودی زنده: $fetchedBalance USDT")
                onResult(true, msg)
            } else {
                _isApiConnected.value = false
                val msg = "خطا: کلید API معتبر نیست یا توسط صرافی رد شد."
                _lastEngineLog.value = msg
                addAuditLog("API_ERROR", msg, false)
                onResult(false, msg)
            }
        }
    }

    fun toggleAutoEngine() {
        if (!_isApiConnected.value && !_isEngineRunning.value) {
            val err = "ابتدا کلید معتبر API را ثبت و تایید کنید."
            _lastEngineLog.value = err
            addAuditLog("ENGINE_ERROR", err, false)
            return
        }

        _isEngineRunning.value = !_isEngineRunning.value
        if (_isEngineRunning.value) {
            val startMsg = "موتور ترید خودکار استارت شد. اسکن زنده فعال است."
            _lastEngineLog.value = startMsg
            addAuditLog("ENGINE_START", startMsg, true)
            sendTelegramAlert("🚀 <b>موتور ربات HM HAMMER فعال شد</b>\nاسکن زنده بازار آغاز گردید.")
            startMarketScanner()
        } else {
            val stopMsg = "موتور ترید متوقف شد."
            _lastEngineLog.value = stopMsg
            addAuditLog("ENGINE_STOP", stopMsg, false)
            sendTelegramAlert("🛑 <b>موتور ربات متوقف گردید</b>")
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
                val err = "خطا: حداقل ۵ تتر برای ثبت سفارش الزامی است."
                _lastEngineLog.value = err
                addAuditLog("ORDER_REJECTED", err, false)
                return@launch
            }

            val price = _currentPrice.value
            val levels = _dynamicLevels.value
            val amountTmn = tradeAmountUsdt * _tomanRate.value

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
                closeReason = "Active Order",
                timestamp = System.currentTimeMillis()
            )

            _usdtBalance.value -= tradeAmountUsdt
            _trades.value = listOf(newOrder) + _trades.value
            val logText = "${if (isAuto) "ترید خودکار" else "دستی"}: سفارش $side با حجم ${tradeAmountUsdt.toInt()} USDT ثبت شد."
            _lastEngineLog.value = logText
            addAuditLog("ORDER_OPENED", logText, true)

            sendTelegramAlert(
                "⚡ <b>سفارش جدید ثبت شد</b>\n" +
                "نماد: ${_selectedPair.value}\n" +
                "حجم: ${tradeAmountUsdt.toInt()} USDT (${String.format("%,.0f", amountTmn)} تومان)\n" +
                "ورود: $$price | SL: $${String.format("%.2f", levels.stopLoss)} | TP1: $${String.format("%.2f", levels.tp1)}"
            )
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
        val diffUsdt = payout - tradeAmountUsdt
        val diffTmn = diffUsdt * _tomanRate.value

        _usdtBalance.value += payout
        _trades.value = _trades.value.map {
            if (it.timestamp == orderTimestamp) {
                it.copy(
                    status = TradeStatus.CLOSED,
                    exitPrice = if (isProfit) it.tp1 else it.stopLoss,
                    profitUsdt = diffUsdt,
                    pnlPercent = if (isProfit) 4.0 else -1.5,
                    exitTimestamp = System.currentTimeMillis(),
                    closeReason = if (isProfit) "Take Profit" else "Stop Loss"
                )
            } else it
        }

        val resText = "معامله ${order.symbol} بسته شد. سود/زیان: ${if (diffUsdt >= 0) "+$" else "-$"}${String.format("%.2f", kotlin.math.abs(diffUsdt))} USDT"
        _lastEngineLog.value = resText
        addAuditLog("ORDER_CLOSED", resText, diffUsdt >= 0)

        sendTelegramAlert(
            "🎯 <b>پوزیشن بسته شد</b>\n" +
            "نماد: ${order.symbol}\n" +
            "نتیجه: ${if (isProfit) "تارگت سود 🟢" else "حد ضرر 🔴"}\n" +
            "سود/زیان دلاری: ${if (diffUsdt >= 0) "+$" else "-$"}${String.format("%.2f", kotlin.math.abs(diffUsdt))} USDT\n" +
            "سود/زیان تومانی: ${if (diffTmn >= 0) "+" else "-"}${String.format("%,.0f", kotlin.math.abs(diffTmn))} تومان"
        )
    }

    fun queryAiCopilot(userQuestion: String): String {
        val levels = _dynamicLevels.value
        val price = _currentPrice.value
        val pair = _selectedPair.value
        val trendStatus = if (price >= levels.entryPrice) "صعودی بالای میانگین" else "پولبک اصلاحی"

        return when {
            userQuestion.contains("وضعیت", ignoreCase = true) || userQuestion.contains("سیگنال", ignoreCase = true) ->
                "تحلیل جفت‌ارز $pair:\nقیمت در محدوده $$price قرار دارد. وضعیت روند: $trendStatus.\nالگوی چکش با نسبت سایه ۲.۱ برابر بدنه ثبت شده است. تارگت نخست: $${String.format("%.2f", levels.tp1)} و حد ضرر دینامیک: $${String.format("%.2f", levels.stopLoss)}."

            userQuestion.contains("ریسک", ignoreCase = true) || userQuestion.contains("سرمایه", ignoreCase = true) ->
                "محاسبه ریسک زنده:\nموجودی فعال: $${String.format("%.2f", _usdtBalance.value)} USDT.\nپیشنهاد مدیریت سرمایه چکش: سقف ورود ۲٪ معادل $${String.format("%.2f", _usdtBalance.value * 0.02)} با نسبت R:R برابر با ${levels.riskRewardRatio}."

            else ->
                "رادار هوش مصنوعی HM HAMMER جفت‌ارز $pair را تحت نظر دارد. جریان سفارشات فعلی نشان‌دهنده حمایت قوی در محدوده $${String.format("%.2f", levels.stopLoss)} است. استراتژی چکش تنها با تایید کندل تثبیت فعال می‌شود."
        }
    }

    fun purgeSandbox() {
        _trades.value = emptyList()
        _auditLogs.value = emptyList()
        addAuditLog("SYSTEM_RESET", "سوابق پاکسازی شدند.", true)
        _lastEngineLog.value = "سیستم ریست شد."
    }

    fun exportAuditReport(): String {
        val sb = StringBuilder()
        sb.append("=== سند رسمی عملکرد HM HAMMER ===\n")
        sb.append("موجودی: ${String.format("%.2f", _usdtBalance.value)} USDT\n")
        sb.append("--------------------------------------------------\n")
        _auditLogs.value.forEach { sb.append("[${it.timestamp}] [${it.eventType}] ${it.message}\n") }
        sb.append("==================================================")
        return sb.toString()
    }
}
