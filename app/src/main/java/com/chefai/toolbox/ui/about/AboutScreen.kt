package com.chefai.toolbox.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chefai.toolbox.ui.common.CosmicBackground
import com.chefai.toolbox.ui.common.HudCard
import com.chefai.toolbox.ui.common.HudTopBar
import com.chefai.toolbox.ui.theme.CosmicBlack
import com.chefai.toolbox.ui.theme.IceWhite
import com.chefai.toolbox.ui.theme.IronmanGold
import com.chefai.toolbox.ui.theme.JarvisCyan
import com.chefai.toolbox.ui.theme.NebulaPurple
import com.chefai.toolbox.ui.theme.StardustMuted

@Composable
fun AboutScreen(onBack: () -> Unit) {
    CosmicBackground {
        Scaffold(
            topBar = { HudTopBar(title = "關於專案", onBack = onBack) },
            containerColor = Color.Transparent,
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item { ProfileHeader() }
                item {
                    InfoCard(
                        emoji = "🎯",
                        glow = JarvisCyan,
                        title = "專案理念",
                        content = "由真正的廚師打造的 AI 料理工具，不只是生成食譜，\n而是給你廚師等級的備註、技巧與時間控管。",
                    )
                }
                item {
                    InfoCard(
                        emoji = "⚡",
                        glow = IronmanGold,
                        title = "三大核心特色",
                        content = "• 零後端架構 — 直接呼叫 OpenAI API，使用者自付費\n" +
                            "• 隱私由你掌握 — API Key 只存在你的手機中\n" +
                            "• 廚師差異化 — 小白師傅 20 年經驗的真實備註",
                    )
                }
                item {
                    InfoCard(
                        emoji = "🛠️",
                        glow = NebulaPurple,
                        title = "技術規格",
                        content = "• AI：OpenAI Chat Completions + Vision API\n" +
                            "• 串流：Server-Sent Events (OkHttp)\n" +
                            "• UI：Jetpack Compose Material 3\n" +
                            "• 語言：Kotlin + Coroutines Flow",
                    )
                }
                item {
                    InfoCard(
                        emoji = "💰",
                        glow = IronmanGold,
                        title = "費用說明",
                        content = "你使用自己的 OpenAI API Key，費用直接從你的 OpenAI 帳戶扣除。\n" +
                            "開發者不會收取任何費用也無法看到你的資料。\n" +
                            "參考：每道食譜約 NT$0.01-0.05，熱量分析約 NT$0.05-0.15。",
                    )
                }
                item {
                    InfoCard(
                        emoji = "📦",
                        glow = JarvisCyan,
                        title = "版本",
                        content = "v0.6.2 · 2026-04-27\n連線 OpenAI 雲端 API",
                    )
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun ProfileHeader() {
    HudCard(glowColor = IronmanGold, contentPadding = 24.dp) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(
                        brush = Brush.radialGradient(
                            listOf(IronmanGold.copy(alpha = 0.6f), CosmicBlack),
                        ),
                        shape = CircleShape,
                    )
                    .border(2.dp, IronmanGold, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Restaurant,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = IronmanGold,
                )
            }
            Text(
                text = "廚師 AI 工具箱",
                color = IronmanGold,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                ),
            )
            Text(
                text = "20 年廚師經驗 × Android 開發",
                color = IceWhite,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "CHEF · CODE · COSMIC",
                color = StardustMuted,
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 3.sp),
            )
        }
    }
}

@Composable
private fun InfoCard(emoji: String, glow: Color, title: String, content: String) {
    HudCard(glowColor = glow) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "$emoji $title",
                color = glow,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = content,
                color = IceWhite,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
