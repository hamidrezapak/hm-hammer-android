package com.example.domain.usecase.account

import com.example.core.result.AppResult
import com.example.domain.repository.AccountRepository
import javax.inject.Inject

class VerifyApiKeyUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(apiKey: String): AppResult<Double> {
        val trimmed = apiKey.trim()
        if (trimmed.length < 8) {
            return AppResult.Error("کلید معتبر نیست (حداقل ۸ کاراکتر)")
        }
        val result = accountRepository.verifyApiKey(trimmed)
        if (result is AppResult.Success) {
            accountRepository.saveApiKey(trimmed)
        }
        return result
    }
}
