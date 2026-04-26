package com.chefai.toolbox.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chefai.toolbox.ui.common.CosmicBackground
import com.chefai.toolbox.ui.common.HudCard
import com.chefai.toolbox.ui.common.HudTopBar
import com.chefai.toolbox.ui.theme.IceWhite
import com.chefai.toolbox.ui.theme.IronmanGold
import com.chefai.toolbox.ui.theme.JarvisCyan
import com.chefai.toolbox.ui.theme.NebulaPurple
import com.chefai.toolbox.ui.theme.SignalGreen
import com.chefai.toolbox.ui.theme.StardustMuted

private data class FeatureCardData(
    val title: String,
    val description: String,
    val emoji: String,
    val icon: ImageVector,
    val glow: Color,
    val onClick: () -> Unit,
)

@Composable
fun HomeScreen(
    onNavigateFridge: () -> Unit,
    onNavigateCalories: () -> Unit,
    onNavigateHistory: () -> Unit,
    onNavigateSettings: () -> Unit,
    onNavigateAbout: () -> Unit,
) {
    val features = listOf(
        FeatureCardData(
            title = "冰箱食譜生成器",
            description = "選擇冰箱現有食材，召喚 2-3 道廚師級食譜",
            emoji = "🥬",
            icon = Icons.Filled.Kitchen,
            glow = JarvisCyan,
            onClick = onNavigateFridge,
        ),
        FeatureCardData(
            title = "熱量雷達",
            description = "對餐點拍照或選照片，AI 即刻估算熱量與營養素",
            emoji = "📸",
            icon = Icons.Filled.LocalFireDepartment,
            glow = IronmanGold,
            onClick = onNavigateCalories,
        ),
        FeatureCardData(
            title = "我的紀錄",
            description = "翻閱料理食譜與熱量分析，可單筆或批次刪除",
            emoji = "📚",
            icon = Icons.Filled.History,
            glow = NebulaPurple,
            onClick = onNavigateHistory,
        ),
    )

    CosmicBackground {
        Scaffold(
            topBar = {
                HudTopBar(
                    title = "CHEF AI",
                    actions = {
                        IconButton(onClick = onNavigateSettings) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "設定",
                                tint = IronmanGold,
                            )
                        }
                        IconButton(onClick = onNavigateAbout) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "關於",
                                tint = JarvisCyan,
                            )
                        }
                    },
                )
            },
            containerColor = Color.Transparent,
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item { HeroBanner() }
                items(features.size) { index ->
                    FeatureCard(features[index])
                }
                item { StatusFooter() }
            }
        }
    }
}

@Composable
private fun HeroBanner() {
    HudCard(glowColor = IronmanGold, contentPadding = 20.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(SignalGreen, CircleShape),
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = "SYSTEM ONLINE",
                    color = SignalGreen,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                    ),
                )
            }
            Text(
                text = "⚡ 小白師傅",
                color = IronmanGold,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                ),
            )
            Text(
                text = "由真廚師打造的 AI 料理工具\n連線 OpenAI · 零後端 · 隱私由你掌握",
                color = IceWhite,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun FeatureCard(data: FeatureCardData) {
    HudCard(glowColor = data.glow, onClick = data.onClick, contentPadding = 18.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        brush = Brush.radialGradient(
                            listOf(data.glow.copy(alpha = 0.35f), Color.Transparent),
                        ),
                        shape = CircleShape,
                    )
                    .border(1.dp, data.glow.copy(alpha = 0.8f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = data.emoji,
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = data.title,
                    color = IceWhite,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = data.description,
                    color = StardustMuted,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = data.glow,
            )
        }
    }
}

@Composable
private fun StatusFooter() {
    HudCard(glowColor = NebulaPurple, contentPadding = 14.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "◇ SYSTEM",
                color = NebulaPurple,
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
            )
            Text(
                text = "引擎：OpenAI Chat + Vision API\n驅動：20 年實戰廚師經驗",
                color = IceWhite,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

