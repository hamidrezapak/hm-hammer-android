package com.example.domain.usecase.account

import com.example.core.result.AppResult
import com.example.domain.repository.AccountRepository
import javax.inject.Inject

class FetchBalanceUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(asset: String = "USDT"): AppResult<Double> {
        return accountRepository.fetchBalance(asset)
    }
}
