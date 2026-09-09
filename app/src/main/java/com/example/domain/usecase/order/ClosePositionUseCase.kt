package com.example.domain.usecase.order

import com.example.core.result.AppResult
import com.example.domain.model.OrderSide
import com.example.domain.model.TradeOrder
import com.example.domain.model.TradeStatus
import com.example.domain.repository.OrderRepository
import javax.inject.Inject

class ClosePositionUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    data class ClosedResult(val updatedOrder: TradeOrder, val profitUsdt: Double, val pnlPercent: Double)

    suspend operator fun invoke(order: TradeOrder): AppResult<ClosedResult> {
        val priceResult = orderRepository.getCurrentMarketPrice(order.symbol)
        val currentPrice = when (priceResult) {
            is AppResult.Success -> priceResult.data
            is AppResult.Error -> return AppResult.Error(
                "دریافت قیمت لحظه‌ای برای بستن پوزیشن ناموفق بود: ${priceResult.message}"
            )
        }

        val closingSide = if (order.side == OrderSide.BUY) OrderSide.SELL else OrderSide.BUY
        val orderResult = orderRepository.placeOrder(order.symbol, closingSide, order.quantity, currentPrice)

        return when (orderResult) {
            is AppResult.Error -> AppResult.Error("بستن پوزیشن ناموفق: ${orderResult.message}")
            is AppResult.Success -> {
                val entryValue = order.entryPrice * order.quantity
                val exitValue = currentPrice * order.quantity
                val profitUsdt = if (order.side == OrderSide.BUY) exitValue - entryValue else entryValue - exitValue
                val pnlPercent = (profitUsdt / entryValue) * 100.0

                val updated = order.copy(
                    exitPrice = currentPrice,
                    status = TradeStatus.CLOSED,
                    closedAt = System.currentTimeMillis()
                )
                AppResult.Success(ClosedResult(updated, profitUsdt, pnlPercent))
            }
        }
    }
}
