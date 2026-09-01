package com.example.engine

import com.example.model.Candle
import com.example.model.PatternType
import com.example.model.TradingSignal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sin
import kotlin.random.Random

data class PairTicker(
    val symbol: String,
    val baseAsset: String,
    val price: Double,
    val change24h: Double,
    val spread: Double,
    val atr: Double,
    val rsi: Double,
    val ema200: Double,
    val volume: Double,
    val volSma: Double,
    val isVolumeSpike: Boolean,
    val isBtcBullish: Boolean,
    val lastSignal: TradingSignal? = null,
    val status: String = "SCANNING"
)

class MarketDataManager {

    private val _tickers = MutableStateFlow<List<PairTicker>>(emptyList())
    val tickers = _tickers.asStateFlow()

    private val pairHistory = mutableMapOf<String, MutableList<Candle>>()

    init {
        initializePairs()
    }

    private fun initializePairs() {
        val basePrices = mapOf(
            "BTC/USDT" to 91420.0,
            "ETH/USDT" to 3480.0,
            "SOL/USDT" to 198.5,
            "TRX/USDT" to 0.245,
            "XRP/USDT" to 1.85,
            "BNB/USDT" to 680.0,
            "DOGE/USDT" to 0.28,
            "ADA/USDT" to 0.82,
            "TON/USDT" to 5.45,
            "AVAX/USDT" to 32.5,
            "NEAR/USDT" to 6.10,
            "LINK/USDT" to 18.2,
            "SUI/USDT" to 3.42,
            "PEPE/USDT" to 0.0000185,
            "SHIB/USDT" to 0.0000245
        )

        val list = mutableListOf<PairTicker>()
        basePrices.forEach { (symbol, basePrice) ->
            val candles = generateInitialCandles(basePrice, 60)
            pairHistory[symbol] = candles.toMutableList()

            val calculated = StrategyEngineV2.calculateIndicators(candles)
            val last = calculated.last()
            val spread = basePrice * 0.0004
            val volSma = last.volSma ?: (last.volume * 0.9)
            val isSpike = last.volume >= (volSma * 1.3)

            list.add(
                PairTicker(
                    symbol = symbol,
                    baseAsset = symbol.substringBefore("/"),
                    price = last.close,
                    change24h = Random.nextDouble(-3.5, 6.8),
                    spread = spread,
                    atr = last.atr ?: (basePrice * 0.018),
                    rsi = last.rsi ?: 48.0,
                    ema200 = last.ema200 ?: (basePrice * 0.98),
                    volume = last.volume,
                    volSma = volSma,
                    isVolumeSpike = isSpike,
                    isBtcBullish = true
                )
            )
        }
        _tickers.value = list
    }

    private fun generateInitialCandles(basePrice: Double, count: Int): List<Candle> {
        val list = mutableListOf<Candle>()
        var current = basePrice * 0.94
        val now = System.currentTimeMillis()

        for (i in 0 until count) {
            val wave = sin(i * 0.25) * (basePrice * 0.015)
            val noise = (Random.nextDouble() - 0.48) * (basePrice * 0.01)
            val open = current
            val close = (open + wave * 0.2 + noise).coerceAtLeast(basePrice * 0.000001)
            val high = maxOf(open, close) + Random.nextDouble() * (basePrice * 0.008)
            val low = minOf(open, close) - Random.nextDouble() * (basePrice * 0.008)
            val volume = Random.nextDouble(50000.0, 500000.0)

            list.add(
                Candle(
                    timestamp = now - (count - i) * 15 * 60 * 1000L,
                    open = open,
                    high = high,
                    low = low,
                    close = close,
                    volume = volume
                )
            )
            current = close
        }
        return list
    }

    fun getCandlesForPair(symbol: String): List<Candle> {
        val candles = pairHistory[symbol] ?: emptyList()
        return StrategyEngineV2.calculateIndicators(candles)
    }

    /**
     * Ticks the market forward, updates prices, evaluates StrategyEngineV2 5-pillar rules
     */
    fun tickMarket(onSignalDetected: (TradingSignal) -> Unit): List<PairTicker> {
        val currentTickers = _tickers.value.toMutableList()
        val btcBullish = (currentTickers.find { it.symbol == "BTC/USDT" }?.price ?: 90000.0) >= 88000.0

        for (i in currentTickers.indices) {
            val ticker = currentTickers[i]
            val candles = pairHistory[ticker.symbol] ?: continue
            val lastCandle = candles.last()

            // Realistic price delta
            val deltaPct = (Random.nextDouble() - 0.49) * 0.008
            val newClose = (lastCandle.close * (1.0 + deltaPct)).coerceAtLeast(0.000001)
            val newHigh = maxOf(lastCandle.high, newClose)
            val newLow = minOf(lastCandle.low, newClose)
            val newVolume = lastCandle.volume + Random.nextDouble(1000.0, 8000.0)

            // Update latest candle in buffer
            candles[candles.size - 1] = lastCandle.copy(
                high = newHigh,
                low = newLow,
                close = newClose,
                volume = newVolume
            )

            // Check if we should close candle and start a new 15m bar
            if (Random.nextInt(12) == 0) {
                // Occasionally inject a deliberate Hammer or Shooting Star for rich live demo experience
                val makeSpecialPattern = Random.nextInt(20) == 0
                val open = newClose
                val body = newClose * 0.003
                val close = if (makeSpecialPattern && btcBullish) open + body else open - body
                val high = if (makeSpecialPattern && btcBullish) close + (body * 0.1) else close + (body * 2.5)
                val low = if (makeSpecialPattern && btcBullish) open - (body * 2.5) else open - (body * 0.1)
                val vol = if (makeSpecialPattern) (ticker.volSma * 1.65) else Random.nextDouble(60000.0, 300000.0)

                candles.add(
                    Candle(
                        timestamp = System.currentTimeMillis(),
                        open = open,
                        high = high,
                        low = low,
                        close = close,
                        volume = vol
                    )
                )
                if (candles.size > 100) candles.removeAt(0)
            }

            // Recalculate indicators
            val calculated = StrategyEngineV2.calculateIndicators(candles)
            val latest = calculated.last()
            val spread = newClose * 0.00035
            val volSma = latest.volSma ?: (latest.volume * 0.9)
            val isSpike = latest.volume >= (volSma * 1.3)

            // 5-Pillar Strategy Engine evaluation
            val signal = StrategyEngineV2.evaluateSignal(
                symbol = ticker.symbol,
                candles = calculated,
                btcBullish = btcBullish,
                currentSpread = spread
            )

            if (signal != null) {
                onSignalDetected(signal)
            }

            currentTickers[i] = ticker.copy(
                price = newClose,
                change24h = ticker.change24h + (deltaPct * 10),
                spread = spread,
                atr = latest.atr ?: (newClose * 0.015),
                rsi = latest.rsi ?: 50.0,
                ema200 = latest.ema200 ?: (newClose * 0.98),
                volume = latest.volume,
                volSma = volSma,
                isVolumeSpike = isSpike,
                isBtcBullish = btcBullish,
                lastSignal = signal ?: ticker.lastSignal,
                status = if (signal != null) "🚨 ${signal.action} SIGNAL" else "SCANNING OK"
            )
        }

        _tickers.value = currentTickers
        return currentTickers
    }
}
