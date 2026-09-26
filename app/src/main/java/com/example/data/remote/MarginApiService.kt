package com.example.data.remote

import com.example.data.remote.dto.MarginCloseRequest
import com.example.data.remote.dto.MarginOpenRequest
import com.example.data.remote.dto.MarginPositionResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface MarginApiService {

    @POST("positions")
    suspend fun openPosition(
        @Header("X-API-Key") apiKey: String,
        @Body body: MarginOpenRequest
    ): MarginPositionResponse

    @PATCH("positions/{id}/close")
    suspend fun closePosition(
        @Header("X-API-Key") apiKey: String,
        @Path("id") id: Long,
        @Body body: MarginCloseRequest
    ): MarginPositionResponse
}
