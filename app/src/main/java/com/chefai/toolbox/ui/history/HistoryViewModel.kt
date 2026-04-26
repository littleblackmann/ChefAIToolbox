package com.chefai.toolbox.ui.history

import androidx.lifecycle.ViewModel
import com.chefai.toolbox.data.RecipeHistoryStore
import com.chefai.toolbox.data.RecipeRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HistoryUiState(
    val selectedRecord: RecipeRecord? = null,
    val selectionMode: Boolean = false,
    val checkedIds: Set<Long> = emptySet(),
    val pendingDeleteId: Long? = null,
    val showBatchDeleteDialog: Boolean = false,
    val showClearDialog: Boolean = false,
)

class HistoryViewModel(private val store: RecipeHistoryStore) : ViewModel() {

    val records: StateFlow<List<RecipeRecord>> = store.records

    private val _ui = MutableStateFlow(HistoryUiState())
    val ui: StateFlow<HistoryUiState> = _ui.asStateFlow()

    fun open(record: RecipeRecord) {
        if (_ui.value.selectionMode) {
            toggleChecked(record.id)
        } else {
            _ui.value = _ui.value.copy(selectedRecord = record)
        }
    }

    fun closeDetail() {
        _ui.value = _ui.value.copy(selectedRecord = null)
    }

    fun enterSelection(initialId: Long? = null) {
        _ui.value = _ui.value.copy(
            selectionMode = true,
            checkedIds = initialId?.let { setOf(it) } ?: emptySet(),
        )
    }

    fun exitSelection() {
        _ui.value = _ui.value.copy(selectionMode = false, checkedIds = emptySet())
    }

    fun toggleChecked(id: Long) {
        val cur = _ui.value.checkedIds
        val next = if (id in cur) cur - id else cur + id
        _ui.value = _ui.value.copy(checkedIds = next)
    }

    fun selectAll() {
        _ui.value = _ui.value.copy(checkedIds = records.value.map { it.id }.toSet())
    }

    fun askDelete(id: Long) {
        _ui.value = _ui.value.copy(pendingDeleteId = id)
    }

    fun dismissDeleteDialog() {
        _ui.value = _ui.value.copy(pendingDeleteId = null)
    }

    fun confirmDelete() {
        val id = _ui.value.pendingDeleteId ?: return
        store.remove(id)
        val cur = _ui.value
        _ui.value = cur.copy(
            pendingDeleteId = null,
            selectedRecord = if (cur.selectedRecord?.id == id) null else cur.selectedRecord,
            checkedIds = cur.checkedIds - id,
        )
    }

    fun askBatchDelete() {
        if (_ui.value.checkedIds.isEmpty()) return
        _ui.value = _ui.value.copy(showBatchDeleteDialog = true)
    }

    fun dismissBatchDeleteDialog() {
        _ui.value = _ui.value.copy(showBatchDeleteDialog = false)
    }

    fun confirmBatchDelete() {
        val ids = _ui.value.checkedIds
        store.removeMany(ids)
        _ui.value = _ui.value.copy(
            showBatchDeleteDialog = false,
            selectionMode = false,
            checkedIds = emptySet(),
        )
    }

    fun askClear() {
        _ui.value = _ui.value.copy(showClearDialog = true)
    }

    fun dismissClearDialog() {
        _ui.value = _ui.value.copy(showClearDialog = false)
    }

    fun confirmClear() {
        store.clear()
        _ui.value = HistoryUiState()
    }
}
