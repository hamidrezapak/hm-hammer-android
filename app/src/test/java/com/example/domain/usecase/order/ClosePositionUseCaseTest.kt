package com.example.domain.usecase.order

import com.example.core.result.AppResult
import com.example.domain.model.OrderSide
import com.example.domain.model.TradeOrder
import com.example.domain.model.TradeStatus
import com.example.domain.repository.OrderRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * پیاده‌سازی جعلی (Fake) از OrderRepository — بدون نیاز به کتابخانه mock،
 * فقط برای کنترل کامل مقادیر بازگشتی در تست.
 */
private class FakeOrderRepository(
    private val marketPrice: AppResult<Double>,
    private val placeOrderResult: AppResult<String> = AppResult.Success("FAKE_ORDER_ID")
) : OrderRepository {
    override suspend fun placeOrder(symbol: String, side: OrderSide, quantity: Double, price: Double): AppResult<String> =
        placeOrderResult

    override suspend fun getCurrentMarketPrice(symbol: String): AppResult<Double> = marketPrice
}

class ClosePositionUseCaseTest {

    private fun sampleOrder(side: OrderSide, entryPrice: Double) = TradeOrder(
        id = "1",
        symbol = "BTCUSDT",
        side = side,
        entryPrice = entryPrice,
        quantity = 0.01,
        status = TradeStatus.OPEN,
        openedAt = System.currentTimeMillis()
    )

    @Test
    fun `BUY position closed at higher price yields positive profit`() = runTest {
        val repo = FakeOrderRepository(marketPrice = AppResult.Success(70000.0))
        val useCase = ClosePositionUseCase(repo)
        val order = sampleOrder(OrderSide.BUY, entryPrice = 65000.0)

        val result = useCase(order)

        assertTrue(result is AppResult.Success)
        val closed = (result as AppResult.Success).data
        assertTrue("سود باید مثبت باشد چون قیمت بالا رفته", closed.profitUsdt > 0)
        assertEquals(TradeStatus.CLOSED, closed.updatedOrder.status)
        assertEquals(70000.0, closed.updatedOrder.exitPrice)
    }

    @Test
    fun `BUY position closed at lower price yields negative profit`() = runTest {
        val repo = FakeOrderRepository(marketPrice = AppResult.Success(60000.0))
        val useCase = ClosePositionUseCase(repo)
        val order = sampleOrder(OrderSide.BUY, entryPrice = 65000.0)

        val result = useCase(order)

        assertTrue(result is AppResult.Success)
        val closed = (result as AppResult.Success).data
        assertTrue("زیان باید منفی باشد چون قیمت پایین اومده", closed.profitUsdt < 0)
    }

    @Test
    fun `market price fetch failure returns Error without placing order`() = runTest {
        val repo = FakeOrderRepository(marketPrice = AppResult.Error("قطعی شبکه"))
        val useCase = ClosePositionUseCase(repo)
        val order = sampleOrder(OrderSide.BUY, entryPrice = 65000.0)

        val result = useCase(order)

        assertTrue(result is AppResult.Error)
    }
}
