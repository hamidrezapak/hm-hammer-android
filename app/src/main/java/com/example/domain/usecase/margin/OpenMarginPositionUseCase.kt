package com.example.domain.usecase.margin

import com.example.core.result.AppResult
import com.example.domain.model.MarginPosition
import com.example.domain.model.MarginSide
import com.example.domain.repository.MarginRepository
import javax.inject.Inject

class OpenMarginPositionUseCase @Inject constructor(
    private val marginRepository: MarginRepository
) {
    suspend operator fun invoke(
        market: String,
        side: MarginSide,
        collateral: Double,
        openPrice: Double,
        riskCoef: Int,
        stopLoss: Double?,
        takeProfit: Double?
    ): AppResult<MarginPosition> {
        if (collateral <= 0.0) return AppResult.Error("وثیقه باید بزرگ‌تر از صفر باشد")
        if (riskCoef < 1) return AppResult.Error("ضریب اهرم نامعتبر است")
        return marginRepository.openPosition(market, side, collateral, openPrice, riskCoef, stopLoss, takeProfit)
    }
}
