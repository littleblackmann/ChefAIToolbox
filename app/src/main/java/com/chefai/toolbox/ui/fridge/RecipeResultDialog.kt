package com.chefai.toolbox.ui.fridge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.chefai.toolbox.ui.common.HudScanningIndicator
import com.chefai.toolbox.ui.common.RecipePager
import com.chefai.toolbox.ui.theme.CosmicBlack
import com.chefai.toolbox.ui.theme.ErrorRed
import com.chefai.toolbox.ui.theme.IceWhite
import com.chefai.toolbox.ui.theme.IronmanGold
import com.chefai.toolbox.ui.theme.JarvisCyan
import com.chefai.toolbox.ui.theme.StardustMuted

@Composable
fun RecipeResultDialog(
    state: FridgeUiState,
    onClose: () -> Unit,
) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CosmicBlack.copy(alpha = 0.95f)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 40.dp),
            ) {
                DialogHeader(
                    title = if (state.isGenerating) "小白師傅構思中" else "${state.style.label}風料理",
                    subtitle = servingsDisplay(state.servings),
                    onClose = onClose,
                )
                Spacer(Modifier.height(8.dp))

                when {
                    state.error != null -> ErrorBlock(state.error)
                    state.recipes.isEmpty() && state.isGenerating -> {
                        InitialLoadingBlock()
                    }
                    state.recipes.isNotEmpty() -> {
                        RecipePager(
                            recipes = state.recipes,
                            modifier = Modifier.weight(1f),
                            topBanner = if (state.isGenerating) {
                                {
                                    HudScanningIndicator(label = "⚡ 串流中（${state.recipes.size} 道已成形）")
                                    Spacer(Modifier.height(8.dp))
                                }
                            } else null,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogHeader(title: String, subtitle: String?, onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "⚡ $title",
                color = JarvisCyan,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = IronmanGold,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                )
            }
        }
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "關閉",
                tint = IceWhite,
            )
        }
    }
}

internal fun servingsDisplay(servings: Int): String? = when {
    servings >= 8 -> "👥 8 人份以上"
    servings >= 1 -> "👥 $servings 人份"
    else -> null
}

@Composable
private fun InitialLoadingBlock() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        HudScanningIndicator(label = "⚡ 連線 OpenAI，召喚小白師傅")
        Spacer(Modifier.height(24.dp))
        Text(
            text = "請稍候，料理思考中...",
            color = StardustMuted,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun ErrorBlock(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "⚠️ 發生錯誤",
            color = ErrorRed,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = message,
            color = IceWhite,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
