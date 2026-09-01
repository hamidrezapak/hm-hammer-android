package com.example.model

data class Candle(
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double,
    var ema200: Double? = null,
    var rsi: Double? = null,
    var volSma: Double? = null,
    var atr: Double? = null
)
