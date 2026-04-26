package com.chefai.toolbox.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chefai.toolbox.ChefAIApplication
import com.chefai.toolbox.ui.about.AboutScreen
import com.chefai.toolbox.ui.calories.CaloriesScreen
import com.chefai.toolbox.ui.fridge.FridgeScreen
import com.chefai.toolbox.ui.history.HistoryScreen
import com.chefai.toolbox.ui.home.HomeScreen
import com.chefai.toolbox.ui.settings.SettingsScreen
import com.chefai.toolbox.ui.setup.ApiKeySetupScreen

object ChefRoutes {
    const val SETUP = "setup"
    const val HOME = "home"
    const val FRIDGE = "fridge"
    const val CALORIES = "calories"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
}

@Composable
fun ChefNavGraph() {
    val navController = rememberNavController()
    val app = LocalContext.current.applicationContext as ChefAIApplication
    val startDestination = if (app.container.settings.hasApiKey) ChefRoutes.HOME else ChefRoutes.SETUP

    NavHost(navController = navController, startDestination = startDestination) {
        composable(ChefRoutes.SETUP) {
            ApiKeySetupScreen(
                onReady = {
                    navController.navigate(ChefRoutes.HOME) {
                        popUpTo(ChefRoutes.SETUP) { inclusive = true }
                    }
                },
            )
        }
        composable(ChefRoutes.HOME) {
            HomeScreen(
                onNavigateFridge = { navController.navigate(ChefRoutes.FRIDGE) },
                onNavigateCalories = { navController.navigate(ChefRoutes.CALORIES) },
                onNavigateHistory = { navController.navigate(ChefRoutes.HISTORY) },
                onNavigateSettings = { navController.navigate(ChefRoutes.SETTINGS) },
                onNavigateAbout = { navController.navigate(ChefRoutes.ABOUT) },
            )
        }
        composable(ChefRoutes.FRIDGE) {
            FridgeScreen(onBack = { navController.popBackStack() })
        }
        composable(ChefRoutes.CALORIES) {
            CaloriesScreen(onBack = { navController.popBackStack() })
        }
        composable(ChefRoutes.HISTORY) {
            HistoryScreen(onBack = { navController.popBackStack() })
        }
        composable(ChefRoutes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onApiKeyReset = {
                    navController.navigate(ChefRoutes.SETUP) {
                        popUpTo(ChefRoutes.HOME) { inclusive = true }
                    }
                },
            )
        }
        composable(ChefRoutes.ABOUT) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }
}
