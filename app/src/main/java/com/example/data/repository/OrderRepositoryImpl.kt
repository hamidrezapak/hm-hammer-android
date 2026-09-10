package com.example.data.repository

import com.example.core.result.AppResult
import com.example.data.remote.WallexApiService
import com.example.data.remote.dto.WallexOrderRequest
import com.example.domain.model.OrderSide
import com.example.domain.repository.OrderRepository
import com.example.network.SecureKeyStore
import retrofit2.HttpException
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.round

class OrderRepositoryImpl @Inject constructor(
    private val api: WallexApiService
) : OrderRepository {

    override suspend fun placeOrder(
        symbol: String,
        side: OrderSide,
        quantity: Double,
        price: Double
    ): AppResult<String> {
        val apiKey = SecureKeyStore.getKey()
        if (apiKey.isBlank()) return AppResult.Error("کلید API تنظیم نشده است")

        return try {
            val upper = symbol.uppercase(Locale.ROOT)
            val symbolData = try {
                api.getMarkets().result.symbols[upper]
            } catch (e: Exception) {
                null
            }

            val precision = symbolData?.quoteAssetPrecision ?: fallbackPricePrecision(upper)
            val step = symbolData?.baseAssetPrecision ?: fallbackStepSize(upper)
            val minNotional = symbolData?.minNotional ?: fallbackMinNotional(upper)

            val cleanQty = floorTo(quantity, step)
            val cleanPrice = roundTo(price, precision)

            if (cleanQty * cleanPrice < minNotional) {
                return AppResult.Error(
                    "ارزش سفارش (${cleanQty * cleanPrice}) کمتر از حداقل مجاز ($minNotional) است"
                )
            }

            val body = WallexOrderRequest(
                symbol = upper,
                side = side.name,
                type = "LIMIT",
                price = String.format(Locale.US, "%.${precision}f", cleanPrice),
                quantity = String.format(Locale.US, "%.${step}f", cleanQty)
            )
            val response = api.placeOrder(apiKey, body)
            val orderId = response.result.orderId ?: response.result.id ?: "OK"
            AppResult.Success(orderId)
        } catch (e: HttpException) {
            AppResult.Error(mapHttpError(e), e)
        } catch (e: IOException) {
            AppResult.Error("خطای شبکه: ${e.localizedMessage}", e)
        }
    }

    override suspend fun getCurrentMarketPrice(symbol: String): AppResult<Double> = try {
        val upper = symbol.uppercase(Locale.ROOT)
        val markets = api.getMarkets()
        val price = markets.result.symbols[upper]?.stats?.lastPrice?.toDoubleOrNull()
        if (price != null) {
            AppResult.Success(price)
        } else {
            AppResult.Error("نماد $symbol یافت نشد یا قیمت در دسترس نیست")
        }
    } catch (e: HttpException) {
        AppResult.Error(mapHttpError(e), e)
    } catch (e: IOException) {
        AppResult.Error("خطای شبکه: ${e.localizedMessage}", e)
    }

    private fun floorTo(value: Double, decimals: Int): Double {
        val factor = 10.0.pow(decimals)
        return floor(value * factor) / factor
    }

    private fun roundTo(value: Double, decimals: Int): Double {
        val factor = 10.0.pow(decimals)
        return round(value * factor) / factor
    }

    private fun fallbackStepSize(symbol: String): Int = when {
        symbol.startsWith("BTC") -> 6
        symbol.startsWith("ETH") -> 5
        symbol.startsWith("SOL") -> 3
        symbol.startsWith("DOGE") || symbol.startsWith("TRX") -> 1
        else -> 4
    }

    private fun fallbackPricePrecision(symbol: String): Int =
        if (symbol.endsWith("TMN")) 0 else 2

    private fun fallbackMinNotional(symbol: String): Double = when {
        symbol.endsWith("TMN") -> 200_000.0
        symbol.endsWith("USDT") -> 1.0
        else -> 1.0
    }

    private fun mapHttpError(e: HttpException): String = when (e.code()) {
        401, 403 -> "احراز هویت ناموفق — کلید API نامعتبر است"
        else -> "خطای صرافی (${e.code()}): ${e.message()}"
    }
}
