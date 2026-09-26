package com.example.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MarginOpenRequest(
    val collateral: String,
    val market: String,
    @Json(name = "open_price") val openPrice: String,
    @Json(name = "risk_coef") val riskCoef: String,
    val side: String,
    @Json(name = "stop_loss") val stopLoss: String? = null,
    @Json(name = "take_profit") val takeProfit: String? = null
)

@JsonClass(generateAdapter = true)
data class MarginCloseRequest(val price: String)

@JsonClass(generateAdapter = true)
data class MarginPositionResponse(val result: MarginPositionDto)

@JsonClass(generateAdapter = true)
data class MarginAmountDto(val currency: String? = null, val value: String? = null)

@JsonClass(generateAdapter = true)
data class MarginPositionDto(
    val id: Long? = null,
    val market: String? = null,
    @Json(name = "call_price") val callPrice: String? = null,
    @Json(name = "liquidity_price") val liquidityPrice: String? = null,
    @Json(name = "close_price") val closePrice: String? = null,
    @Json(name = "close_filled_price") val closeFilledPrice: String? = null,
    @Json(name = "close_reason") val closeReason: String? = null,
    val collateral: MarginAmountDto? = null,
    @Json(name = "closed_at") val closedAt: String? = null
)
