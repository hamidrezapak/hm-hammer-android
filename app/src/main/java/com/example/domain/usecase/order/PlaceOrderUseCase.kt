package com.example.domain.usecase.order

import com.example.core.result.AppResult
import com.example.domain.model.OrderSide
import com.example.domain.repository.OrderRepository
import javax.inject.Inject

class PlaceOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(
        symbol: String,
        side: OrderSide,
        quantity: Double,
        price: Double
    ): AppResult<String> {
        if (quantity <= 0.0) {
            return AppResult.Error("حجم سفارش باید بزرگ‌تر از صفر باشد")
        }
        return orderRepository.placeOrder(symbol, side, quantity, price)
    }
}
