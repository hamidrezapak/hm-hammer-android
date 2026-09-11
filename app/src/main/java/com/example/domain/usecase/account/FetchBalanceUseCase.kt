package com.example.domain.usecase.account

import com.example.core.result.AppResult
import com.example.domain.repository.AccountRepository
import com.example.network.SecureKeyStore
import javax.inject.Inject

class FetchBalanceUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(): AppResult<Double> {
        val apiKey = SecureKeyStore.getKey()
        if (apiKey.isBlank()) return AppResult.Error("کلید API تنظیم نشده است")
        return accountRepository.verifyApiKey(apiKey)
    }
}
