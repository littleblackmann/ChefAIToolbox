package com.chefai.toolbox.ui.calories

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chefai.toolbox.data.CalorieHistoryStore
import com.chefai.toolbox.data.ChefAIRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

data class CaloriesUiState(
    val image: Bitmap? = null,
    val note: String = "",
    val output: String = "",
    val isAnalyzing: Boolean = false,
    val error: String? = null,
)

class CaloriesViewModel(
    private val repo: ChefAIRepository,
    private val historyStore: CalorieHistoryStore,
) : ViewModel() {

    private val _state = MutableStateFlow(CaloriesUiState())
    val state: StateFlow<CaloriesUiState> = _state.asStateFlow()

    private var job: Job? = null

    fun setImage(bitmap: Bitmap?) {
        _state.value = _state.value.copy(image = bitmap, output = "", error = null)
    }

    fun setNote(value: String) {
        _state.value = _state.value.copy(note = value)
    }

    fun analyze() {
        val current = _state.value
        val image = current.image
        if (current.isAnalyzing || image == null) return
        job?.cancel()
        _state.value = current.copy(isAnalyzing = true, output = "", error = null)

        job = viewModelScope.launch {
            repo.calorieVisionStream(image = image, userNote = current.note.takeIf { it.isNotBlank() })
                .catch { e ->
                    _state.value = _state.value.copy(
                        isAnalyzing = false,
                        error = e.message ?: "分析失敗",
                    )
                }
                .onCompletion { cause ->
                    val finished = _state.value
                    _state.value = finished.copy(isAnalyzing = false)
                    if (cause == null && finished.output.isNotBlank() && finished.error == null) {
                        val img = finished.image
                        if (img != null) {
                            val parsed = parseSummary(finished.output)
                            historyStore.add(
                                bitmap = img,
                                dishName = parsed.dishName,
                                caloriesText = parsed.caloriesText,
                                rawOutput = finished.output,
                            )
                        }
                    }
                }
                .collect { partial ->
                    _state.value = _state.value.copy(output = _state.value.output + partial)
                }
        }
    }

    fun reset() {
        job?.cancel()
        _state.value = CaloriesUiState()
    }

    private data class ParsedSummary(val dishName: String, val caloriesText: String)

    private fun parseSummary(output: String): ParsedSummary {
        var dish = "未命名料理"
        var calories = ""
        for (raw in output.lines()) {
            val line = raw.trim()
            if (dish == "未命名料理" && line.startsWith("📸")) {
                dish = line.removePrefix("📸").trim().ifBlank { dish }
            }
            if (calories.isBlank() && line.startsWith("🔥")) {
                calories = line.removePrefix("🔥").trim()
            }
            if (dish != "未命名料理" && calories.isNotBlank()) break
        }
        return ParsedSummary(dish, calories)
    }
}
