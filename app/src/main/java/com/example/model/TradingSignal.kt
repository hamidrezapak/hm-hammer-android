package com.example.model

enum class SignalAction {
    BUY,   // LONG (Hammer)
    SELL   // SHORT (Shooting Star)
}

enum class PatternType {
    BULLISH_HAMMER,
    BEARISH_SHOOTING_STAR
}

data class TradingSignal(
    val symbol: String,
    val action: SignalAction,
    val patternType: PatternType,
    val entryPrice: Double,
    val stopLoss: Double,
    val tp1: Double,
    val tp2: Double,
    val tp3: Double,
    val atr: Double,
    val rsi: Double,
    val ema200: Double,
    val volumeSpike: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val timeframe: String = "15m",
    val status: String = "ACTIVE"
)
