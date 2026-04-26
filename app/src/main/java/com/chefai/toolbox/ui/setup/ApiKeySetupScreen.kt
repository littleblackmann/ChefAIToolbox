package com.chefai.toolbox.ui.setup

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chefai.toolbox.ChefAIApplication
import com.chefai.toolbox.ui.ChefViewModelFactory
import com.chefai.toolbox.ui.common.CosmicBackground
import com.chefai.toolbox.ui.common.HudCard
import com.chefai.toolbox.ui.common.HudPrimaryButton
import com.chefai.toolbox.ui.common.HudSecondaryButton
import com.chefai.toolbox.ui.common.HudTopBar
import com.chefai.toolbox.ui.theme.ErrorRed
import com.chefai.toolbox.ui.theme.IceWhite
import com.chefai.toolbox.ui.theme.IronmanGold
import com.chefai.toolbox.ui.theme.JarvisCyan
import com.chefai.toolbox.ui.theme.NebulaPurple
import com.chefai.toolbox.ui.theme.SignalGreen
import com.chefai.toolbox.ui.theme.StardustMuted

@Composable
fun ApiKeySetupScreen(
    onReady: () -> Unit,
    onBack: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val app = context.applicationContext as ChefAIApplication
    val viewModel: ApiKeySetupViewModel = viewModel(factory = ChefViewModelFactory(app.container))
    val state by viewModel.state.collectAsStateWithLifecycle()
    val clipboard = LocalClipboardManager.current

    LaunchedEffect(state.success) {
        if (state.success) {
            onReady()
            viewModel.reset()
        }
    }

    CosmicBackground {
        Scaffold(
            topBar = {
                HudTopBar(
                    title = "API KEY 連線",
                    onBack = onBack,
                )
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
                IntroCard()

                StepCard(
                    step = 1,
                    icon = Icons.AutoMirrored.Filled.Launch,
                    title = "前往 OpenAI 官網",
                    body = "打開瀏覽器前往 platform.openai.com/api-keys\n" +
                        "沒有帳號的話先註冊（用 Google 登入最快）。",
                    actionLabel = "開啟官網",
                    onAction = {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://platform.openai.com/api-keys")),
                        )
                    },
                )
                StepCard(
                    step = 2,
                    icon = Icons.Filled.Shield,
                    title = "綁定信用卡並儲值",
                    body = "Billing → Add payment method\n" +
                        "建議先儲值 5 美金試水溫，日常用量一個月大約 1-3 美金。",
                    actionLabel = "開啟付款設定",
                    onAction = {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://platform.openai.com/settings/organization/billing/overview")),
                        )
                    },
                )
                StepCard(
                    step = 3,
                    icon = Icons.Filled.Key,
                    title = "建立 API Key 並複製",
                    body = "Create new secret key → 取名 ChefAI → 建立後立刻「複製」\n" +
                        "⚠️ 這串 sk- 開頭的 Key 只會顯示一次，複製後貼到下面。",
                    actionLabel = null,
                    onAction = null,
                )

                HudCard(glowColor = JarvisCyan) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "貼上 API KEY",
                            color = JarvisCyan,
                            style = MaterialTheme.typography.labelLarge,
                        )
                        OutlinedTextField(
                            value = state.apiKeyInput,
                            onValueChange = viewModel::onInputChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("sk-...", color = StardustMuted) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = IceWhite,
                                unfocusedTextColor = IceWhite,
                                focusedBorderColor = JarvisCyan,
                                unfocusedBorderColor = StardustMuted,
                                cursorColor = JarvisCyan,
                            ),
                        )
                        HudSecondaryButton(
                            text = "從剪貼簿貼上",
                            onClick = {
                                clipboard.getText()?.text?.let { viewModel.onInputChange(it) }
                            },
                        )
                        if (state.errorMessage != null) {
                            Text(
                                text = "⚠️ ${state.errorMessage}",
                                color = ErrorRed,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }

                if (state.isValidating) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(
                            color = JarvisCyan,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                        )
                        Spacer(Modifier.size(12.dp))
                        Text("驗證中...", color = JarvisCyan)
                    }
                } else {
                    HudPrimaryButton(
                        text = "🚀 啟動小白師傅",
                        onClick = viewModel::validateAndSave,
                        enabled = state.apiKeyInput.isNotBlank(),
                    )
                }

                PrivacyNote()
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun IntroCard() {
    HudCard(glowColor = IronmanGold) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "歡迎加入小白師傅 AI 廚房",
                color = IronmanGold,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
            )
            Text(
                text = "為了讓你使用最強的 AI 生成食譜與分析熱量，\n需要你自己的 OpenAI API Key。\n金流走你自己的帳戶，費用你自己掌控。",
                color = IceWhite,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun StepCard(
    step: Int,
    icon: ImageVector,
    title: String,
    body: String,
    actionLabel: String?,
    onAction: (() -> Unit)?,
) {
    HudCard(glowColor = NebulaPurple) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(JarvisCyan.copy(alpha = 0.15f), CircleShape)
                        .border(1.dp, JarvisCyan, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = step.toString(),
                        color = JarvisCyan,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    )
                }
                Spacer(Modifier.size(12.dp))
                Icon(imageVector = icon, contentDescription = null, tint = IronmanGold)
                Spacer(Modifier.size(8.dp))
                Text(
                    text = title,
                    color = IceWhite,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Text(
                text = body,
                color = IceWhite,
                style = MaterialTheme.typography.bodyMedium,
            )
            if (actionLabel != null && onAction != null) {
                HudSecondaryButton(text = actionLabel, onClick = onAction)
            }
        }
    }
}

@Composable
private fun PrivacyNote() {
    HudCard(glowColor = SignalGreen.copy(alpha = 0.5f)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "🔒 隱私說明",
                color = SignalGreen,
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                text = "你的 API Key 只儲存在這支手機本地，\n不會上傳到任何伺服器。\n所有請求直接從手機送到 OpenAI。",
                color = IceWhite,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
