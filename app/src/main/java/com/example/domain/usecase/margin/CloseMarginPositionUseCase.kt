package com.example.domain.usecase.margin

import com.example.core.result.AppResult
import com.example.domain.model.ClosedMarginResult
import com.example.domain.repository.MarginRepository
import javax.inject.Inject

class CloseMarginPositionUseCase @Inject constructor(
    private val marginRepository: MarginRepository
) {
    suspend operator fun invoke(id: Long, price: Double): AppResult<ClosedMarginResult> =
        marginRepository.closePosition(id, price)
}
