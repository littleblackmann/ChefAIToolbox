package com.chefai.toolbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import com.chefai.toolbox.ui.navigation.ChefNavGraph
import com.chefai.toolbox.ui.theme.ChefAIToolboxTheme
import com.chefai.toolbox.ui.theme.CosmicBlack

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cosmicScrim = CosmicBlack.toArgb()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(cosmicScrim),
            navigationBarStyle = SystemBarStyle.dark(cosmicScrim),
        )
        setContent {
            ChefAIToolboxTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ChefNavGraph()
                }
            }
        }
    }
}
