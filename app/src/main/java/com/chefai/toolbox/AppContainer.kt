package com.chefai.toolbox

import android.content.Context
import com.chefai.toolbox.ai.OpenAIClient
import com.chefai.toolbox.data.AppSettings
import com.chefai.toolbox.data.CalorieHistoryStore
import com.chefai.toolbox.data.ChefAIRepository
import com.chefai.toolbox.data.RecipeHistoryStore

class AppContainer(context: Context) {
    val settings: AppSettings = AppSettings(context.applicationContext)
    val openAIClient: OpenAIClient = OpenAIClient()
    val repository: ChefAIRepository = ChefAIRepository(openAIClient, settings)
    val historyStore: RecipeHistoryStore = RecipeHistoryStore(context.applicationContext)
    val calorieHistoryStore: CalorieHistoryStore = CalorieHistoryStore(context.applicationContext)
}
