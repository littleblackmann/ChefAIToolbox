package com.chefai.toolbox.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chefai.toolbox.ChefAIApplication
import com.chefai.toolbox.ui.ChefViewModelFactory
import com.chefai.toolbox.ui.common.ConfirmDialog
import com.chefai.toolbox.ui.common.CosmicBackground
import com.chefai.toolbox.ui.common.HudCard
import com.chefai.toolbox.ui.common.HudSecondaryButton
import com.chefai.toolbox.ui.common.HudTopBar
import com.chefai.toolbox.ui.theme.IceWhite
import com.chefai.toolbox.ui.theme.IronmanGold
import com.chefai.toolbox.ui.theme.JarvisCyan
import com.chefai.toolbox.ui.theme.NebulaPurple
import com.chefai.toolbox.ui.theme.SignalGreen
import com.chefai.toolbox.ui.theme.StardustMuted

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onApiKeyReset: () -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val app = context.applicationContext as ChefAIApplication
    val viewModel: SettingsViewModel = viewModel(factory = ChefViewModelFactory(app.container))
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showClearDialog by remember { mutableStateOf(false) }

    androidx.compose.runtime.LaunchedEffect(state.apiKeyCleared) {
        if (state.apiKeyCleared) {
            viewModel.resetClearFlag()
            onApiKeyReset()
        }
    }

    if (showClearDialog) {
        ConfirmDialog(
            title = "清除 API Key？",
            message = "清除後需要重新輸入金鑰才能繼續使用 AI 功能。",
            confirmText = "清除",
            onConfirm = {
                showClearDialog = false
                viewModel.clearApiKey()
            },
            onDismiss = { showClearDialog = false },
        )
    }

    CosmicBackground {
        Scaffold(
            topBar = {
                HudTopBar(title = "系統設定", onBack = onBack)
            },
            containerColor = Color.Transparent,
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ApiKeyCard(
                    maskedKey = state.maskedApiKey,
                    onClear = { showClearDialog = true },
                )

                ModelInfoCard(
                    recipeModelDisplay = state.recipeModelDisplay,
                    recipeModelId = state.recipeModelId,
                    visionModelDisplay = state.visionModelDisplay,
                    visionModelId = state.visionModelId,
                )

                CostTipCard()

                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun ApiKeyCard(maskedKey: String, onClear: () -> Unit) {
    HudCard(glowColor = JarvisCyan) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "API KEY 狀態",
                color = JarvisCyan,
                style = MaterialTheme.typography.labelLarge,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(SignalGreen, CircleShape),
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = if (maskedKey.isNotEmpty()) "已連線：$maskedKey" else "尚未設定",
                    color = IceWhite,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            HudSecondaryButton(
                text = "清除並重新連線",
                onClick = onClear,
            )
        }
    }
}

@Composable
private fun ModelInfoCard(
    recipeModelDisplay: String,
    recipeModelId: String,
    visionModelDisplay: String,
    visionModelId: String,
) {
    HudCard(glowColor = IronmanGold) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "🤖 AI 模型配置（已最佳化）",
                color = IronmanGold,
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                text = "為了品質與成本平衡，已自動為每項功能配置最合適的模型，無需手動切換。",
                color = StardustMuted,
                style = MaterialTheme.typography.bodySmall,
            )
            ModelInfoRow(
                feature = "🗄️ 冰箱食譜生成",
                modelDisplay = recipeModelDisplay,
                modelId = recipeModelId,
                hint = "速度與成本最佳化",
            )
            ModelInfoRow(
                feature = "📸 熱量雷達",
                modelDisplay = visionModelDisplay,
                modelId = visionModelId,
                hint = "精準度優先",
            )
        }
    }
}

@Composable
private fun ModelInfoRow(
    feature: String,
    modelDisplay: String,
    modelId: String,
    hint: String,
) {
    val borderColor = JarvisCyan.copy(alpha = 0.4f)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .background(JarvisCyan.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = feature,
            color = IceWhite,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "→ $modelDisplay",
                color = JarvisCyan,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = "($hint)",
                color = StardustMuted,
                style = MaterialTheme.typography.labelMedium,
            )
        }
        Text(
            text = "model ID: $modelId",
            color = NebulaPurple,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun CostTipCard() {
    HudCard(glowColor = NebulaPurple) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "💰 費用參考",
                color = NebulaPurple,
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                text = "依照 OpenAI 官方計價，每次食譜生成約 NT$0.01-0.05，\n" +
                    "熱量分析（含圖片）約 NT$0.30-0.50（旗艦模型，精準度優先）。\n" +
                    "實際費用以你的 OpenAI 帳戶為準。",
                color = IceWhite,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
