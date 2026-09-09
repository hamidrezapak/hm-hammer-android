package com.example.domain.repository

import com.example.core.result.AppResult
import com.example.domain.model.OrderSide

interface OrderRepository {
    suspend fun placeOrder(
        symbol: String,
        side: OrderSide,
        quantity: Double,
        price: Double
    ): AppResult<String>

    suspend fun getCurrentMarketPrice(symbol: String): AppResult<Double>
}
