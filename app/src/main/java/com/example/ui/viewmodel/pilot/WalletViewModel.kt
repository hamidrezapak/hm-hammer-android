package com.example.ui.viewmodel.pilot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.result.AppResult
import com.example.domain.usecase.account.VerifyApiKeyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val verifyApiKeyUseCase: VerifyApiKeyUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    fun verifyAndSaveKey(apiKey: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isVerifying = true, message = null)

            when (val result = verifyApiKeyUseCase(apiKey)) {
                is AppResult.Success -> _uiState.value = _uiState.value.copy(
                    isVerifying = false,
                    isConnected = true,
                    usdtBalance = result.data,
                    message = "اتصال برقرار شد. موجودی: ${result.data} USDT"
                )
                is AppResult.Error -> _uiState.value = _uiState.value.copy(
                    isVerifying = false,
                    isConnected = false,
                    message = result.message
                )
            }
        }
    }
}

data class WalletUiState(
    val isVerifying: Boolean = false,
    val isConnected: Boolean = false,
    val usdtBalance: Double = 0.0,
    val message: String? = null
)
