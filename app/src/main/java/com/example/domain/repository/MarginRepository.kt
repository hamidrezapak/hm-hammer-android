package com.example.domain.repository

import com.example.core.result.AppResult
import com.example.domain.model.ClosedMarginResult
import com.example.domain.model.MarginPosition
import com.example.domain.model.MarginSide

interface MarginRepository {
    suspend fun openPosition(
        market: String,
        side: MarginSide,
        collateral: Double,
        openPrice: Double,
        riskCoef: Int,
        stopLoss: Double?,
        takeProfit: Double?
    ): AppResult<MarginPosition>

    suspend fun closePosition(id: Long, price: Double): AppResult<ClosedMarginResult>
}
