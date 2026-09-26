package com.example.service

import android.util.Log
import com.example.network.WallexLiveClient
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PaperTrader(
    private val symbol: String,
    private val onStatus: (String) -> Unit,
    private val quoteAsset: String = "USDT"
) {
    private val startBalance: Double = if (quoteAsset == "TMN") 6_000_000.0 else 100.0
    private val minOrder: Double = if (quoteAsset == "TMN") 100_000.0 else 1.5

    private var quote = startBalance
    private var qty = 0.0
    private var entry = 0.0
    private var entrySpend = 0.0
    private var dayStartEquity = startBalance
    private var dayKey = dayKeyNow()
    private var halted = false
    private var cooldownUntil = 0L
    private var trades = 0
    private var wins = 0
    private var prevFast = 0.0
    private var prevSlow = 0.0
    private val prices = ArrayDeque<Double>()

    private companion object {
        const val TAG = "PaperTrader"
        const val FEE = 0.002
        const val SLIP = 0.0005
        const val SIZE_PCT = 0.10
        const val TP_PCT = 1.5
        const val SL_PCT = -2.0
        const val DAILY_LOSS_PCT = -3.0
        const val COOLDOWN_MS = 5 * 60 * 1000L
        const val POLL_MS = 10_000L
    }

    private fun dayKeyNow(): String = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
    private fun equity(p: Double): Double = quote + qty * p
    private fun sma(n: Int): Double = prices.toList().takeLast(n).average()

    private fun f(x: Double): String =
        if (quoteAsset == "TMN") String.format(Locale.US, "%,.0f", x)
        else String.format(Locale.US, "%.2f", x)

    suspend fun run() {
        var failures = 0
        while (true) {
            val p = WallexLiveClient.fetchMarketPrice(symbol).getOrNull()
            if (p == null || p <= 0.0) {
                failures++
                onStatus("Paper: خطا در دریافت قیمت")
                delay((POLL_MS * failures).coerceAtMost(60_000L))
                continue
            }
            failures = 0
            step(p)
            delay(POLL_MS)
        }
    }

    private fun status(p: Double, note: String) {
        val wr = if (trades > 0) wins * 100 / trades else 0
        onStatus("Paper($quoteAsset) | $note | ارزش: ${f(equity(p))} | معاملات: $trades | برد: $wr%")
    }

    private fun step(p: Double) {
        val today = dayKeyNow()
        if (today != dayKey) {
            dayKey = today
            dayStartEquity = equity(p)
            halted = false
        }
        prices.addLast(p)
        if (prices.size > 60) prices.removeFirst()

        val dd = (equity(p) - dayStartEquity) / dayStartEquity * 100.0
        if (dd <= DAILY_LOSS_PCT && !halted) {
            halted = true
            if (qty > 0.0) sell(p, "سقف ضرر روزانه")
            onStatus("Paper($quoteAsset): سقف ضرر روزانه فعال شد، توقف تا فردا")
            return
        }
        if (prices.size < 30) {
            onStatus("Paper($quoteAsset): جمع‌آوری داده ${prices.size}/30")
            return
        }

        val fast = sma(10)
        val slow = sma(30)
        val crossedUp = prevSlow != 0.0 && prevFast <= prevSlow && fast > slow
        prevFast = fast
        prevSlow = slow

        when {
            qty > 0.0 -> {
                val pct = (p - entry) / entry * 100.0
                if (pct >= TP_PCT) sell(p, "حد سود")
                else if (pct <= SL_PCT) sell(p, "حد ضرر")
                else status(p, "در پوزیشن ${String.format(Locale.US, "%.2f", pct)}%")
            }
            halted -> status(p, "متوقف تا فردا")
            System.currentTimeMillis() < cooldownUntil -> status(p, "استراحت پس از فروش")
            crossedUp -> buy(p)
            else -> status(p, "منتظر سیگنال")
        }
    }

    private fun buy(p: Double) {
        val spend = minOf(quote, equity(p) * SIZE_PCT)
        if (spend < minOrder) {
            onStatus("Paper($quoteAsset): موجودی فرضی کافی نیست")
            return
        }
        val fill = p * (1 + SLIP)
        qty = spend * (1 - FEE) / fill
        quote -= spend
        entry = fill
        entrySpend = spend
        Log.i(TAG, "BUY $symbol spend=${f(spend)} fill=${f(fill)}")
        status(p, "خرید در ${f(fill)}")
    }

    private fun sell(p: Double, reason: String) {
        val fill = p * (1 - SLIP)
        val net = qty * fill * (1 - FEE)
        val pnl = net - entrySpend
        quote += net
        qty = 0.0
        entry = 0.0
        entrySpend = 0.0
        trades++
        if (pnl > 0) wins++
        cooldownUntil = System.currentTimeMillis() + COOLDOWN_MS
        Log.i(TAG, "SELL $symbol reason=$reason pnl=${f(pnl)}")
        status(p, "فروش ($reason) سود/زیان ${f(pnl)}")
    }
}
