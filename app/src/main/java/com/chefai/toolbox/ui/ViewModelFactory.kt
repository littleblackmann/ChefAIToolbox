package com.chefai.toolbox.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chefai.toolbox.AppContainer
import com.chefai.toolbox.ui.calories.CaloriesViewModel
import com.chefai.toolbox.ui.fridge.FridgeViewModel
import com.chefai.toolbox.ui.history.CalorieHistoryViewModel
import com.chefai.toolbox.ui.history.HistoryViewModel
import com.chefai.toolbox.ui.settings.SettingsViewModel
import com.chefai.toolbox.ui.setup.ApiKeySetupViewModel

class ChefViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(ApiKeySetupViewModel::class.java) ->
            ApiKeySetupViewModel(container.settings, container.repository) as T
        modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
            SettingsViewModel(container.settings) as T
        modelClass.isAssignableFrom(FridgeViewModel::class.java) ->
            FridgeViewModel(container.repository, container.historyStore) as T
        modelClass.isAssignableFrom(CaloriesViewModel::class.java) ->
            CaloriesViewModel(container.repository, container.calorieHistoryStore) as T
        modelClass.isAssignableFrom(HistoryViewModel::class.java) ->
            HistoryViewModel(container.historyStore) as T
        modelClass.isAssignableFrom(CalorieHistoryViewModel::class.java) ->
            CalorieHistoryViewModel(container.calorieHistoryStore) as T
        else -> throw IllegalArgumentException("Unknown ViewModel: $modelClass")
    }
}
