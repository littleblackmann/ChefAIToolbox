package com.chefai.toolbox.ui.settings

import androidx.lifecycle.ViewModel
import com.chefai.toolbox.ai.OpenAIModelCatalog
import com.chefai.toolbox.data.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsUiState(
    val maskedApiKey: String,
    val recipeModelDisplay: String = OpenAIModelCatalog.RECIPE_MODEL_DISPLAY,
    val recipeModelId: String = OpenAIModelCatalog.RECIPE_MODEL,
    val visionModelDisplay: String = OpenAIModelCatalog.VISION_MODEL_DISPLAY,
    val visionModelId: String = OpenAIModelCatalog.VISION_MODEL,
    val apiKeyCleared: Boolean = false,
)

class SettingsViewModel(private val settings: AppSettings) : ViewModel() {

    private val _state = MutableStateFlow(
        SettingsUiState(maskedApiKey = mask(settings.apiKey)),
    )
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    fun clearApiKey() {
        settings.clearApiKey()
        _state.value = _state.value.copy(maskedApiKey = "", apiKeyCleared = true)
    }

    fun resetClearFlag() {
        _state.value = _state.value.copy(apiKeyCleared = false)
    }

    private fun mask(key: String?): String {
        if (key.isNullOrBlank()) return ""
        if (key.length <= 10) return "••••"
        return key.take(6) + "••••" + key.takeLast(4)
    }
}
