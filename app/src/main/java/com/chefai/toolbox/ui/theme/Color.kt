package com.chefai.toolbox.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// 宇宙背景
val CosmicBlack = Color(0xFF0A0E1A)
val DeepSpace = Color(0xFF131829)
val StarfieldCard = Color(0xFF1C2340)
val NebulaEdge = Color(0xFF252D52)

// 強調色
val JarvisCyan = Color(0xFF00E5FF)
val JarvisCyanSoft = Color(0xFF4FC3F7)
val IronmanGold = Color(0xFFFFB800)
val IronmanRed = Color(0xFFE53935)
val NebulaPurple = Color(0xFF8B5CF6)

// 文字
val IceWhite = Color(0xFFE8F0FF)
val StardustMuted = Color(0xFF6B7B99)
val DimStar = Color(0xFF455175)

// 狀態
val SignalGreen = Color(0xFF00FF88)
val WarningAmber = Color(0xFFFFB800)
val ErrorRed = Color(0xFFFF3860)

// 漸層 brush 工廠
object CosmicBrushes {
    val heroGradient: Brush
        get() = Brush.verticalGradient(
            listOf(CosmicBlack, DeepSpace, StarfieldCard)
        )
    val goldGlow: Brush
        get() = Brush.horizontalGradient(
            listOf(IronmanGold, Color(0xFFFF7F00))
        )
    val cyanGlow: Brush
        get() = Brush.horizontalGradient(
            listOf(JarvisCyan, NebulaPurple)
        )
}
