package com.example.domain.usecase.order

import com.example.core.result.AppResult
import com.example.domain.repository.OrderRepository
import javax.inject.Inject

class GetMarketPriceUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(symbol: String): AppResult<Double> =
        orderRepository.getCurrentMarketPrice(symbol)
}
