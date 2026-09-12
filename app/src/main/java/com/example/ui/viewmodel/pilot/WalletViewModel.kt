package com.example.ui.viewmodel.pilot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.result.AppResult
import com.example.domain.usecase.account.FetchBalanceUseCase
import com.example.domain.usecase.account.VerifyApiKeyUseCase
import com.example.network.SecureKeyStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val verifyApiKeyUseCase: VerifyApiKeyUseCase,
    private val fetchBalanceUseCase: FetchBalanceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    init {
        if (SecureKeyStore.getKey().isNotBlank()) {
            refreshAllBalances()
        }
    }

    fun verifyAndSaveKey(apiKey: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isVerifying = true, message = null)

            when (val result = verifyApiKeyUseCase(apiKey)) {
                is AppResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isVerifying = false,
                        isConnected = true,
                        usdtBalance = result.data,
                        message = "اتصال برقرار شد. موجودی: ${result.data} USDT"
                    )
                    fetchOtherBalances()
                }
                is AppResult.Error -> _uiState.value = _uiState.value.copy(
                    isVerifying = false,
                    isConnected = false,
                    message = result.message
                )
            }
        }
    }

    fun refreshAllBalances() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isVerifying = true)
            val usdtResult = fetchBalanceUseCase("USDT")
            if (usdtResult is AppResult.Success) {
                _uiState.value = _uiState.value.copy(isConnected = true, usdtBalance = usdtResult.data)
            }
            fetchOtherBalances()
            _uiState.value = _uiState.value.copy(isVerifying = false)
        }
    }

    private suspend fun fetchOtherBalances() {
        val tmnResult = fetchBalanceUseCase("TMN")
        val btcResult = fetchBalanceUseCase("BTC")
        val trxResult = fetchBalanceUseCase("TRX")

        _uiState.value = _uiState.value.copy(
            tmnBalance = (tmnResult as? AppResult.Success)?.data ?: _uiState.value.tmnBalance,
            btcBalance = (btcResult as? AppResult.Success)?.data ?: _uiState.value.btcBalance,
            trxBalance = (trxResult as? AppResult.Success)?.data ?: _uiState.value.trxBalance
        )
    }
}

data class WalletUiState(
    val isVerifying: Boolean = false,
    val isConnected: Boolean = false,
    val usdtBalance: Double = 0.0,
    val tmnBalance: Double = 0.0,
    val btcBalance: Double = 0.0,
    val trxBalance: Double = 0.0,
    val message: String? = null
)
