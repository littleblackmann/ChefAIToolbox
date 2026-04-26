package com.chefai.toolbox.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CosmicColors = darkColorScheme(
    primary = JarvisCyan,
    onPrimary = CosmicBlack,
    primaryContainer = StarfieldCard,
    onPrimaryContainer = JarvisCyan,
    secondary = IronmanGold,
    onSecondary = CosmicBlack,
    secondaryContainer = NebulaEdge,
    onSecondaryContainer = IronmanGold,
    tertiary = NebulaPurple,
    onTertiary = IceWhite,
    background = CosmicBlack,
    onBackground = IceWhite,
    surface = DeepSpace,
    onSurface = IceWhite,
    surfaceVariant = StarfieldCard,
    onSurfaceVariant = StardustMuted,
    outline = NebulaEdge,
    outlineVariant = DimStar,
    error = ErrorRed,
    onError = IceWhite,
)

@Composable
fun ChefAIToolboxTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = CosmicColors,
        typography = ChefTypography,
        content = content,
    )
}
