package com.chefai.toolbox.data

import android.content.Context
import android.content.SharedPreferences

class AppSettings(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    var apiKey: String?
        get() = prefs.getString(KEY_API_KEY, null)?.takeIf { it.isNotBlank() }
        set(value) {
            prefs.edit().apply {
                if (value.isNullOrBlank()) remove(KEY_API_KEY) else putString(KEY_API_KEY, value)
                apply()
            }
        }

    val hasApiKey: Boolean get() = !apiKey.isNullOrBlank()

    fun clearApiKey() {
        prefs.edit().remove(KEY_API_KEY).apply()
    }

    companion object {
        private const val FILE_NAME = "chef_ai_settings"
        private const val KEY_API_KEY = "openai_api_key"
    }
}
