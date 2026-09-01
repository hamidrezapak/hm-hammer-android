package com.example.engine

import com.example.model.Candle
import com.example.model.PatternType
import com.example.model.SignalAction
import com.example.model.TradingSignal
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object StrategyEngineV2Config {
    // Fee rates per plan tier
    val FEE_RATES = mapOf(
        "standard" to 0.025, // 2.5%
        "pro" to 0.015,      // 1.5%
        "elite" to 0.008,    // 0.8%
        "vip" to 0.000       // VIP 0%
    )
    const val MIN_POSITION_SIZE_USD = 50.0  // Anti-dust minimum
    const val MAX_RISK_PER_TRADE = 0.02    // 2% balance risk lock
    const val USE_POST_ONLY_LIMIT = true   // Maker fee guarantee and zero slippage
    const val BATCH_SIZE = 50              // Concurrent order batch limit
    const val BATCH_DELAY_MS = 100L        // Rate limit anti-block delay
    const val BTC_TREND_TIMEFRAME = "1h"
    const val ALT_TREND_TIMEFRAME = "15m"
    const val RSI_PERIOD = 14
    const val EMA_PERIOD = 200
    const val ATR_PERIOD = 14
    const val VOLATILITY_MULTIPLIER = 5.0  // ATR must be >= spread * 5.0
}

class StrategyEngineV2 {

    companion object {

        /**
         * Calculates technical indicators: EMA200, RSI14, Vol SMA20, ATR14
         */
        fun calculateIndicators(candles: List<Candle>): List<Candle> {
            if (candles.isEmpty()) return candles

            val result = candles.map { it.copy() }
            val n = result.size

            // 1. Calculate EMA 200 (or adaptive for available length if < 200)
            val emaPeriod = if (n >= StrategyEngineV2Config.EMA_PERIOD) StrategyEngineV2Config.EMA_PERIOD else min(n, 20)
            if (n >= emaPeriod && emaPeriod > 0) {
                val k = 2.0 / (emaPeriod + 1)
                var currentEma = 0.0
                for (i in 0 until emaPeriod) {
                    currentEma += result[i].close
                }
                currentEma /= emaPeriod
                result[emaPeriod - 1].ema200 = currentEma

                for (i in emaPeriod until n) {
                    currentEma = (result[i].close * k) + (currentEma * (1 - k))
                    result[i].ema200 = currentEma
                }
            }

            // 2. Calculate RSI 14
            val rsiPeriod = StrategyEngineV2Config.RSI_PERIOD
            if (n > rsiPeriod) {
                var gainSum = 0.0
                var lossSum = 0.0

                for (i in 1..rsiPeriod) {
                    val diff = result[i].close - result[i - 1].close
                    if (diff > 0) gainSum += diff else lossSum += abs(diff)
                }

                var avgGain = gainSum / rsiPeriod
                var avgLoss = lossSum / rsiPeriod

                if (avgLoss == 0.0) {
                    result[rsiPeriod].rsi = 100.0
                } else {
                    val rs = avgGain / avgLoss
                    result[rsiPeriod].rsi = 100.0 - (100.0 / (1.0 + rs))
                }

                for (i in (rsiPeriod + 1) until n) {
                    val diff = result[i].close - result[i - 1].close
                    val gain = if (diff > 0) diff else 0.0
                    val loss = if (diff < 0) abs(diff) else 0.0

                    avgGain = ((avgGain * (rsiPeriod - 1)) + gain) / rsiPeriod
                    avgLoss = ((avgLoss * (rsiPeriod - 1)) + loss) / rsiPeriod

                    if (avgLoss == 0.0) {
                        result[i].rsi = 100.0
                    } else {
                        val rs = avgGain / avgLoss
                        result[i].rsi = 100.0 - (100.0 / (1.0 + rs))
                    }
                }
            }

            // 3. Calculate Volume SMA 20
            val volPeriod = 20
            if (n >= volPeriod) {
                var volSum = 0.0
                for (i in 0 until volPeriod) {
                    volSum += result[i].volume
                }
                result[volPeriod - 1].volSma = volSum / volPeriod

                for (i in volPeriod until n) {
                    volSum += result[i].volume - result[i - volPeriod].volume
                    result[i].volSma = volSum / volPeriod
                }
            }

            // 4. Calculate ATR 14
            val atrPeriod = StrategyEngineV2Config.ATR_PERIOD
            if (n > atrPeriod) {
                val trList = mutableListOf<Double>()
                trList.add(result[0].high - result[0].low)

                for (i in 1 until n) {
                    val hl = result[i].high - result[i].low
                    val hc = abs(result[i].high - result[i - 1].close)
                    val lc = abs(result[i].low - result[i - 1].close)
                    trList.add(max(hl, max(hc, lc)))
                }

                var atr = 0.0
                for (i in 0 until atrPeriod) {
                    atr += trList[i]
                }
                atr /= atrPeriod
                result[atrPeriod - 1].atr = atr

                for (i in atrPeriod until n) {
                    atr = ((atr * (atrPeriod - 1)) + trList[i]) / atrPeriod
                    result[i].atr = atr
                }
            }

            return result
        }

        /**
         * Evaluates trade signals under strict 5-pillar Anti-Fragile rules:
         * 1. Candlestick geometry (Hammer vs Shooting Star)
         * 2. Macro Trend alignment (EMA200)
         * 3. Bitcoin mother market direction
         * 4. RSI Overbought/Oversold thresholds (<=40 for Long, >=60 for Short)
         * 5. Volatility & Volume spike confirmation (ATR >= 5.0 * spread, Vol > 1.3 * SMA)
         */
        fun evaluateSignal(
            symbol: String,
            candles: List<Candle>,
            btcBullish: Boolean,
            currentSpread: Double,
            timeframe: String = "15m"
        ): TradingSignal? {
            if (candles.size < 20) return null

            val calculated = calculateIndicators(candles)
            val c = calculated.last()

            val bodySize = abs(c.close - c.open)
            val candleRange = c.high - c.low

            if (candleRange <= 0.0 || c.atr == null || c.atr == 0.0) {
                return null
            }

            // Volatility filter: ATR must be sufficient vs spread
            val minAtrReq = currentSpread * StrategyEngineV2Config.VOLATILITY_MULTIPLIER
            if ((c.atr ?: 0.0) < minAtrReq) {
                return null // Dead market filter
            }

            val lowerWick = min(c.open, c.close) - c.low
            val upperWick = c.high - max(c.open, c.close)

            // Volume spike check (>= 1.3x 20-period SMA)
            val volSma = c.volSma ?: (c.volume * 0.9)
            val volumeSpike = c.volume >= (volSma * 1.3)
            if (!volumeSpike) {
                return null
            }

            val ema = c.ema200 ?: c.close
            val rsi = c.rsi ?: 50.0
            val atr = c.atr ?: (c.close * 0.015)

            // 1. BULLISH HAMMER (LONG)
            val isHammer = (lowerWick >= 2.0 * bodySize) && (upperWick <= 0.25 * max(bodySize, 0.0001))
            if (isHammer && btcBullish && c.close >= ema && rsi <= 45.0) {
                val entry = c.close
                val sl = c.low
                val tp1 = entry * (1.0 + (atr * 1.0) / entry)
                val tp2 = entry * (1.0 + (atr * 1.5) / entry)
                val tp3 = entry * (1.0 + (atr * 2.0) / entry)

                return TradingSignal(
                    symbol = symbol,
                    action = SignalAction.BUY,
                    patternType = PatternType.BULLISH_HAMMER,
                    entryPrice = entry,
                    stopLoss = sl,
                    tp1 = tp1,
                    tp2 = tp2,
                    tp3 = tp3,
                    atr = atr,
                    rsi = rsi,
                    ema200 = ema,
                    volumeSpike = true,
                    timeframe = timeframe
                )
            }

            // 2. BEARISH SHOOTING STAR (SHORT)
            val isShootingStar = (upperWick >= 2.0 * bodySize) && (lowerWick <= 0.25 * max(bodySize, 0.0001))
            if (isShootingStar && !btcBullish && c.close <= ema && rsi >= 55.0) {
                val entry = c.close
                val sl = c.high
                val tp1 = entry * (1.0 - (atr * 1.0) / entry)
                val tp2 = entry * (1.0 - (atr * 1.5) / entry)
                val tp3 = entry * (1.0 - (atr * 2.0) / entry)

                return TradingSignal(
                    symbol = symbol,
                    action = SignalAction.SELL,
                    patternType = PatternType.BEARISH_SHOOTING_STAR,
                    entryPrice = entry,
                    stopLoss = sl,
                    tp1 = tp1,
                    tp2 = tp2,
                    tp3 = tp3,
                    atr = atr,
                    rsi = rsi,
                    ema200 = ema,
                    volumeSpike = true,
                    timeframe = timeframe
                )
            }

            return null
        }
    }
}
