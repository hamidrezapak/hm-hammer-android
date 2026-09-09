package com.example.domain.model

data class TradeOrder(
    val id: String,
    val symbol: String,
    val side: OrderSide,
    val entryPrice: Double,
    val exitPrice: Double? = null,
    val quantity: Double,
    val status: TradeStatus,
    val exchangeOrderId: String? = null,
    val openedAt: Long,
    val closedAt: Long? = null
)
