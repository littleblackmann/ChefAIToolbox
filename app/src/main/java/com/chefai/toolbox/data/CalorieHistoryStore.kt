package com.chefai.toolbox.data

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

data class CalorieRecord(
    val id: Long,
    val timestamp: Long,
    val dishName: String,
    val caloriesText: String,
    val rawOutput: String,
    val imagePath: String,
)

class CalorieHistoryStore(context: Context) {

    private val appContext = context.applicationContext

    private val prefs: SharedPreferences =
        appContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    private val photoDir: File = File(appContext.filesDir, PHOTO_DIR).apply {
        if (!exists()) mkdirs()
    }

    private val _records = MutableStateFlow(loadAll())
    val records: StateFlow<List<CalorieRecord>> = _records.asStateFlow()

    /**
     * 寫入照片到內部儲存後新增紀錄；若超過上限會自動刪掉最舊的紀錄與圖檔。
     */
    fun add(
        bitmap: Bitmap,
        dishName: String,
        caloriesText: String,
        rawOutput: String,
    ): CalorieRecord? {
        val id = System.currentTimeMillis()
        val photoFile = File(photoDir, "$id.jpg")
        val ok = try {
            FileOutputStream(photoFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }
        } catch (_: Throwable) {
            false
        }
        if (!ok) {
            photoFile.delete()
            return null
        }
        val record = CalorieRecord(
            id = id,
            timestamp = id,
            dishName = dishName,
            caloriesText = caloriesText,
            rawOutput = rawOutput,
            imagePath = photoFile.absolutePath,
        )
        val combined = listOf(record) + _records.value
        val (kept, dropped) = if (combined.size > MAX_RECORDS) {
            combined.take(MAX_RECORDS) to combined.drop(MAX_RECORDS)
        } else {
            combined to emptyList()
        }
        dropped.forEach { deleteImage(it.imagePath) }
        saveAll(kept)
        _records.value = kept
        return record
    }

    fun remove(id: Long) {
        val target = _records.value.firstOrNull { it.id == id } ?: return
        deleteImage(target.imagePath)
        val remaining = _records.value.filterNot { it.id == id }
        saveAll(remaining)
        _records.value = remaining
    }

    fun removeMany(ids: Set<Long>) {
        if (ids.isEmpty()) return
        _records.value.filter { it.id in ids }.forEach { deleteImage(it.imagePath) }
        val remaining = _records.value.filterNot { it.id in ids }
        saveAll(remaining)
        _records.value = remaining
    }

    fun clear() {
        _records.value.forEach { deleteImage(it.imagePath) }
        saveAll(emptyList())
        _records.value = emptyList()
    }

    private fun deleteImage(path: String) {
        try {
            val f = File(path)
            if (f.exists()) f.delete()
        } catch (_: Throwable) {
            // ignore
        }
    }

    private fun loadAll(): List<CalorieRecord> {
        val raw = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    add(
                        CalorieRecord(
                            id = obj.optLong("id"),
                            timestamp = obj.optLong("timestamp"),
                            dishName = obj.optString("dishName"),
                            caloriesText = obj.optString("caloriesText"),
                            rawOutput = obj.optString("rawOutput"),
                            imagePath = obj.optString("imagePath"),
                        ),
                    )
                }
            }
        } catch (_: Throwable) {
            emptyList()
        }
    }

    private fun saveAll(list: List<CalorieRecord>) {
        val arr = JSONArray()
        list.forEach { r ->
            arr.put(
                JSONObject().apply {
                    put("id", r.id)
                    put("timestamp", r.timestamp)
                    put("dishName", r.dishName)
                    put("caloriesText", r.caloriesText)
                    put("rawOutput", r.rawOutput)
                    put("imagePath", r.imagePath)
                },
            )
        }
        prefs.edit().putString(KEY_HISTORY, arr.toString()).apply()
    }

    companion object {
        private const val FILE_NAME = "chef_ai_calorie_history"
        private const val KEY_HISTORY = "calorie_history_v1"
        private const val PHOTO_DIR = "calorie_photos"
        private const val MAX_RECORDS = 50
    }
}
