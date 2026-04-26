package com.chefai.toolbox.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

data class RecipeRecord(
    val id: Long,
    val timestamp: Long,
    val ingredients: List<String>,
    val style: String,
    val recipes: List<String>,
    val servings: Int = 0,
) {
    /** 顯示用：8 → 「8人+」，0 → null（舊紀錄），其他 → 「N人」 */
    fun servingsLabel(): String? = when {
        servings <= 0 -> null
        servings >= 8 -> "8人+"
        else -> "${servings}人"
    }

    /** 取每道料理的第一行當標題，最多取前三道 */
    fun previewTitles(): List<String> = recipes.map { body ->
        body.lines().firstOrNull { it.isNotBlank() }?.trim().orEmpty()
    }.filter { it.isNotBlank() }
}

class RecipeHistoryStore(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    private val _records = MutableStateFlow(loadAll())
    val records: StateFlow<List<RecipeRecord>> = _records.asStateFlow()

    fun add(record: RecipeRecord) {
        val merged = (listOf(record) + _records.value).take(MAX_RECORDS)
        saveAll(merged)
        _records.value = merged
    }

    fun remove(id: Long) {
        val remaining = _records.value.filterNot { it.id == id }
        saveAll(remaining)
        _records.value = remaining
    }

    fun removeMany(ids: Set<Long>) {
        if (ids.isEmpty()) return
        val remaining = _records.value.filterNot { it.id in ids }
        saveAll(remaining)
        _records.value = remaining
    }

    fun clear() {
        saveAll(emptyList())
        _records.value = emptyList()
    }

    private fun loadAll(): List<RecipeRecord> {
        val raw = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    add(
                        RecipeRecord(
                            id = obj.optLong("id"),
                            timestamp = obj.optLong("timestamp"),
                            ingredients = obj.optJSONArray("ingredients").toStringList(),
                            style = obj.optString("style"),
                            recipes = obj.optJSONArray("recipes").toStringList(),
                            servings = obj.optInt("servings", 0),
                        ),
                    )
                }
            }
        } catch (_: Throwable) {
            emptyList()
        }
    }

    private fun saveAll(list: List<RecipeRecord>) {
        val arr = JSONArray()
        list.forEach { r ->
            arr.put(
                JSONObject().apply {
                    put("id", r.id)
                    put("timestamp", r.timestamp)
                    put("ingredients", JSONArray(r.ingredients))
                    put("style", r.style)
                    put("recipes", JSONArray(r.recipes))
                    put("servings", r.servings)
                },
            )
        }
        prefs.edit().putString(KEY_HISTORY, arr.toString()).apply()
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        val out = ArrayList<String>(length())
        for (i in 0 until length()) out.add(optString(i))
        return out
    }

    companion object {
        private const val FILE_NAME = "chef_ai_history"
        private const val KEY_HISTORY = "recipe_history_v1"
        private const val MAX_RECORDS = 50
    }
}
