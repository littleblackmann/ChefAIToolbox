package com.chefai.toolbox.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chefai.toolbox.data.AppSettings
import com.chefai.toolbox.data.ChefAIRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ApiKeySetupUiState(
    val apiKeyInput: String = "",
    val isValidating: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false,
)

class ApiKeySetupViewModel(
    private val settings: AppSettings,
    private val repository: ChefAIRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ApiKeySetupUiState(apiKeyInput = settings.apiKey.orEmpty()))
    val state: StateFlow<ApiKeySetupUiState> = _state.asStateFlow()

    fun onInputChange(value: String) {
        _state.value = _state.value.copy(apiKeyInput = value.trim(), errorMessage = null)
    }

    fun validateAndSave() {
        val key = _state.value.apiKeyInput.trim()
        if (key.isEmpty()) {
            _state.value = _state.value.copy(errorMessage = "請先貼上你的 API Key")
            return
        }
        if (!key.startsWith("sk-")) {
            _state.value = _state.value.copy(errorMessage = "API Key 通常以 sk- 開頭，請檢查是否貼錯")
            return
        }
        _state.value = _state.value.copy(isValidating = true, errorMessage = null)
        viewModelScope.launch {
            val result = repository.validateApiKey(key)
            result.fold(
                onSuccess = {
                    settings.apiKey = key
                    _state.value = _state.value.copy(isValidating = false, success = true)
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isValidating = false,
                        errorMessage = e.message ?: "驗證失敗",
                    )
                },
            )
        }
    }

    fun reset() {
        _state.value = _state.value.copy(success = false)
    }
}
