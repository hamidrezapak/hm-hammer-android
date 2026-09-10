package com.example.data.repository

import com.example.core.result.AppResult
import com.example.data.remote.WallexApiService
import com.example.domain.repository.AccountRepository
import com.example.network.SecureKeyStore
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val api: WallexApiService
) : AccountRepository {

    override suspend fun verifyApiKey(apiKey: String): AppResult<Double> = try {
        val response = api.getBalances(apiKey.trim())
        val usdt = response.result.balances[USDT_ASSET]?.value?.toDoubleOrNull() ?: 0.0
        AppResult.Success(usdt)
    } catch (e: HttpException) {
        AppResult.Error(mapHttpError(e), e)
    } catch (e: IOException) {
        AppResult.Error("خطای اتصال شبکه: ${e.localizedMessage}", e)
    }

    override suspend fun saveApiKey(apiKey: String) {
        SecureKeyStore.saveKey(apiKey.trim())
    }

    private fun mapHttpError(e: HttpException): String = when (e.code()) {
        401, 403 -> "احراز هویت ناموفق — کلید API نامعتبر است"
        else -> "خطای صرافی (${e.code()}): ${e.message()}"
    }

    companion object {
        private const val USDT_ASSET = "USDT"
    }
}
