package com.example.data.remote

import com.example.data.remote.dto.WallexBalancesResponse
import com.example.data.remote.dto.WallexMarketsResponse
import com.example.data.remote.dto.WallexOrderRequest
import com.example.data.remote.dto.WallexOrderResponse
import com.example.data.remote.dto.WallexOrderStatusResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface WallexApiService {

    @GET("markets")
    suspend fun getMarkets(): WallexMarketsResponse

    @GET("account/balances")
    suspend fun getBalances(@Header("X-API-Key") apiKey: String): WallexBalancesResponse

    @POST("orders")
    suspend fun placeOrder(
        @Header("X-API-Key") apiKey: String,
        @Body body: WallexOrderRequest
    ): WallexOrderResponse

    @GET("orders/{id}")
    suspend fun getOrderStatus(
        @Header("X-API-Key") apiKey: String,
        @Path("id") orderId: String
    ): WallexOrderStatusResponse

    @DELETE("orders/{id}")
    suspend fun cancelOrder(
        @Header("X-API-Key") apiKey: String,
        @Path("id") orderId: String
    )
}
