package com.example.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WallexMarketsResponse(val result: MarketsResult)

@JsonClass(generateAdapter = true)
data class MarketsResult(val symbols: Map<String, SymbolData>)

@JsonClass(generateAdapter = true)
data class SymbolData(
    val stats: SymbolStats? = null,
    val baseAssetPrecision: Int? = null,
    val quoteAssetPrecision: Int? = null,
    val minQty: Double? = null,
    val minNotional: Double? = null
)

@JsonClass(generateAdapter = true)
data class SymbolStats(val lastPrice: String? = null)

@JsonClass(generateAdapter = true)
data class WallexBalancesResponse(val result: BalancesResult)

@JsonClass(generateAdapter = true)
data class BalancesResult(val balances: Map<String, BalanceData>)

@JsonClass(generateAdapter = true)
data class BalanceData(val value: String? = null)

@JsonClass(generateAdapter = true)
data class WallexOrderRequest(
    val symbol: String,
    val side: String,
    val type: String,
    val price: String,
    val quantity: String
)

@JsonClass(generateAdapter = true)
data class WallexOrderResponse(val result: OrderResult)

@JsonClass(generateAdapter = true)
data class OrderResult(
    @Json(name = "order_id") val orderId: String? = null,
    val id: String? = null
)

@JsonClass(generateAdapter = true)
data class WallexOrderStatusResponse(val result: OrderStatusResult)

@JsonClass(generateAdapter = true)
data class OrderStatusResult(val status: String? = null)
