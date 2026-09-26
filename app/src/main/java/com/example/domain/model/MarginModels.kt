package com.example.domain.model

enum class MarginSide { LONG, SHORT }

data class MarginPosition(
    val id: Long,
    val market: String,
    val side: MarginSide,
    val collateral: Double,
    val riskCoef: Int,
    val liquidityPrice: Double?,
    val callPrice: Double?,
    val stopLoss: Double?,
    val takeProfit: Double?
)

data class ClosedMarginResult(
    val closePrice: Double?,
    val closeReason: String?
)
