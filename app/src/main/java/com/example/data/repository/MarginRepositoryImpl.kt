package com.example.data.repository

import com.example.core.result.AppResult
import com.example.data.remote.MarginApiService
import com.example.data.remote.dto.MarginCloseRequest
import com.example.data.remote.dto.MarginOpenRequest
import com.example.domain.model.ClosedMarginResult
import com.example.domain.model.MarginPosition
import com.example.domain.model.MarginSide
import com.example.domain.repository.MarginRepository
import com.example.network.SecureKeyStore
import retrofit2.HttpException
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

class MarginRepositoryImpl @Inject constructor(
    private val api: MarginApiService
) : MarginRepository {

    override suspend fun openPosition(
        market: String,
        side: MarginSide,
        collateral: Double,
        openPrice: Double,
        riskCoef: Int,
        stopLoss: Double?,
        takeProfit: Double?
    ): AppResult<MarginPosition> {
        val apiKey = SecureKeyStore.getKey()
        if (apiKey.isBlank()) return AppResult.Error("کلید API تنظیم نشده است")
        return try {
            val body = MarginOpenRequest(
                collateral = String.format(Locale.US, "%.2f", collateral),
                market = market.uppercase(Locale.ROOT),
                openPrice = String.format(Locale.US, "%.8f", openPrice),
                riskCoef = riskCoef.toString(),
                side = if (side == MarginSide.LONG) "long" else "short",
                stopLoss = stopLoss?.let { String.format(Locale.US, "%.8f", it) },
                takeProfit = takeProfit?.let { String.format(Locale.US, "%.8f", it) }
            )
            val dto = api.openPosition(apiKey, body).result
            val id = dto.id
            if (id == null) {
                AppResult.Error("پوزیشن باز شد ولی شناسه در پاسخ نبود؛ در اپ والکس بررسی کنید")
            } else {
                AppResult.Success(
                    MarginPosition(
                        id = id,
                        market = market.uppercase(Locale.ROOT),
                        side = side,
                        collateral = collateral,
                        riskCoef = riskCoef,
                        liquidityPrice = dto.liquidityPrice?.toDoubleOrNull(),
                        callPrice = dto.callPrice?.toDoubleOrNull(),
                        stopLoss = stopLoss,
                        takeProfit = takeProfit
                    )
                )
            }
        } catch (e: HttpException) {
            AppResult.Error(mapHttpError(e), e)
        } catch (e: IOException) {
            AppResult.Error("خطای شبکه: ${e.localizedMessage}", e)
        } catch (e: Exception) {
            AppResult.Error("خطای غیرمنتظره: ${e.localizedMessage ?: e.javaClass.simpleName}", e)
        }
    }

    override suspend fun closePosition(id: Long, price: Double): AppResult<ClosedMarginResult> {
        val apiKey = SecureKeyStore.getKey()
        if (apiKey.isBlank()) return AppResult.Error("کلید API تنظیم نشده است")
        return try {
            val body = MarginCloseRequest(price = String.format(Locale.US, "%.8f", price))
            val dto = api.closePosition(apiKey, id, body).result
            AppResult.Success(
                ClosedMarginResult(
                    closePrice = (dto.closeFilledPrice ?: dto.closePrice)?.toDoubleOrNull(),
                    closeReason = dto.closeReason
                )
            )
        } catch (e: HttpException) {
            AppResult.Error(mapHttpError(e), e)
        } catch (e: IOException) {
            AppResult.Error("خطای شبکه: ${e.localizedMessage}", e)
        } catch (e: Exception) {
            AppResult.Error("خطای غیرمنتظره: ${e.localizedMessage ?: e.javaClass.simpleName}", e)
        }
    }

    private fun mapHttpError(e: HttpException): String = when (e.code()) {
        401, 403 -> "احراز هویت ناموفق — کلید API نامعتبر است"
        400 -> "درخواست نامعتبر — احتمالاً وثیقه یا اهرم خارج از محدوده مجاز صرافی است"
        404 -> "موقعیت مارجین یافت نشد"
        else -> "خطای صرافی (${e.code()}): ${e.message()}"
    }
}
