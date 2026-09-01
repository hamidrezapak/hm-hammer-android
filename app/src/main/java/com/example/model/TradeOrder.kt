package com.example.model

enum class TradeStatus {
    OPEN,
    FILLED,
    TP1_HIT,
    TP2_HIT,
    TP3_HIT,
    TP4_HIT,
    SL_HIT,
    CLOSED
}

data class TradeOrder(
    val id: Long = 0,
    val userId: String = "89175496",
    val symbol: String,
    val side: String, // BUY / SELL
    val entryPrice: Double,
    val currentPrice: Double,
    val exitPrice: Double = 0.0,
    val stopLoss: Double,
    val tp1: Double,
    val tp2: Double,
    val tp3: Double,
    val tp4: Double = 0.0,
    val amountTmn: Double,
    val leverage: String = "2x",
    val status: TradeStatus = TradeStatus.OPEN,
    val pnlPercent: Double = 0.0,
    val profitUsdt: Double = 0.0,
    val isPostOnly: Boolean = true,
    val entryTimestamp: Long = System.currentTimeMillis(),
    val exitTimestamp: Long = 0L,
    val closeReason: String = "Active",
    val timestamp: Long = System.currentTimeMillis()
)
