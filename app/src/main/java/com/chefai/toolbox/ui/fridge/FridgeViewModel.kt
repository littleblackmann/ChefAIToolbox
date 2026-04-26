package com.chefai.toolbox.ui.fridge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chefai.toolbox.data.ChefAIRepository
import com.chefai.toolbox.data.RecipeHistoryStore
import com.chefai.toolbox.data.RecipeRecord
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

data class FridgeUiState(
    val selectedCategory: IngredientCategory = IngredientCategory.VEGETABLE,
    val selectedIngredients: Set<String> = emptySet(),
    val customIngredientInput: String = "",
    val style: CuisineStyle = CuisineStyle.HOME,
    val recipeCount: Int = 3,
    val servings: Int = 2,
    val rawOutput: String = "",
    val recipes: List<String> = emptyList(),
    val isGenerating: Boolean = false,
    val error: String? = null,
    val showResult: Boolean = false,
)

enum class CuisineStyle(val label: String, val emoji: String) {
    HOME("家常", "🏠"),
    JAPANESE("日式", "🍱"),
    WESTERN("西式", "🍝"),
    HEALTHY("低卡", "🥗"),
    QUICK("快手", "⚡"),
}

class FridgeViewModel(
    private val repo: ChefAIRepository,
    private val history: RecipeHistoryStore,
) : ViewModel() {

    private val _state = MutableStateFlow(FridgeUiState())
    val state: StateFlow<FridgeUiState> = _state.asStateFlow()

    private var job: Job? = null

    fun selectCategory(category: IngredientCategory) {
        _state.value = _state.value.copy(selectedCategory = category)
    }

    fun toggleIngredient(name: String) {
        val set = _state.value.selectedIngredients.toMutableSet()
        if (!set.add(name)) set.remove(name)
        _state.value = _state.value.copy(selectedIngredients = set)
    }

    fun onCustomInputChange(value: String) {
        _state.value = _state.value.copy(customIngredientInput = value)
    }

    fun addCustomIngredient() {
        val trimmed = _state.value.customIngredientInput.trim()
        if (trimmed.isEmpty()) return
        val set = _state.value.selectedIngredients.toMutableSet()
        set.add(trimmed)
        _state.value = _state.value.copy(
            selectedIngredients = set,
            customIngredientInput = "",
        )
    }

    fun removeIngredient(name: String) {
        val set = _state.value.selectedIngredients.toMutableSet()
        set.remove(name)
        _state.value = _state.value.copy(selectedIngredients = set)
    }

    fun setStyle(style: CuisineStyle) {
        _state.value = _state.value.copy(style = style)
    }

    fun setRecipeCount(count: Int) {
        _state.value = _state.value.copy(recipeCount = count.coerceIn(1, 3))
    }

    fun setServings(count: Int) {
        _state.value = _state.value.copy(servings = count.coerceIn(1, 8))
    }

    fun generate() {
        if (_state.value.isGenerating || _state.value.selectedIngredients.isEmpty()) return
        job?.cancel()
        _state.value = _state.value.copy(
            isGenerating = true,
            rawOutput = "",
            recipes = emptyList(),
            error = null,
            showResult = true,
        )

        job = viewModelScope.launch {
            repo.fridgeRecipeStream(
                ingredients = _state.value.selectedIngredients.toList(),
                style = _state.value.style.label,
                recipeCount = _state.value.recipeCount,
                servings = _state.value.servings,
            )
                .catch { e ->
                    _state.value = _state.value.copy(
                        isGenerating = false,
                        error = e.message ?: "生成失敗",
                    )
                }
                .onCompletion { cause ->
                    val finalState = _state.value.copy(isGenerating = false)
                    _state.value = finalState
                    // 只有在沒出錯且有結果時才存紀錄
                    if (cause == null && finalState.error == null && finalState.recipes.isNotEmpty()) {
                        val now = System.currentTimeMillis()
                        history.add(
                            RecipeRecord(
                                id = now,
                                timestamp = now,
                                ingredients = finalState.selectedIngredients.toList(),
                                style = finalState.style.label,
                                recipes = finalState.recipes,
                                servings = finalState.servings,
                            ),
                        )
                    }
                }
                .collect { partial ->
                    val newRaw = _state.value.rawOutput + partial
                    _state.value = _state.value.copy(
                        rawOutput = newRaw,
                        recipes = splitRecipes(newRaw),
                    )
                }
        }
    }

    fun closeResult() {
        job?.cancel()
        _state.value = _state.value.copy(
            showResult = false,
            rawOutput = "",
            recipes = emptyList(),
            error = null,
            isGenerating = false,
        )
    }

    fun clearAllIngredients() {
        _state.value = _state.value.copy(selectedIngredients = emptySet())
    }

    private fun splitRecipes(output: String): List<String> {
        return output.split(Regex("\\n-{3,}\\n"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }
}
