package com.example.domain.repository

import com.example.core.result.AppResult

interface AccountRepository {
    suspend fun verifyApiKey(apiKey: String): AppResult<Double>
    suspend fun saveApiKey(apiKey: String)
}
