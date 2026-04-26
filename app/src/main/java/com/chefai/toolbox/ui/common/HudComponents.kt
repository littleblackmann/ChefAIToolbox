package com.chefai.toolbox.ui.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chefai.toolbox.ui.theme.CosmicBlack
import com.chefai.toolbox.ui.theme.CosmicBrushes
import com.chefai.toolbox.ui.theme.DeepSpace
import com.chefai.toolbox.ui.theme.IronmanGold
import com.chefai.toolbox.ui.theme.JarvisCyan
import com.chefai.toolbox.ui.theme.NebulaEdge
import com.chefai.toolbox.ui.theme.StarfieldCard
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun CosmicBackground(
    modifier: Modifier = Modifier,
    showParticles: Boolean = true,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicBrushes.heroGradient),
    ) {
        if (showParticles) StarfieldParticles()
        content()
    }
}

@Composable
private fun StarfieldParticles() {
    val stars = remember {
        List(60) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 1.6f + 0.4f,
                phase = Random.nextFloat() * 6.28f,
                speed = Random.nextFloat() * 0.8f + 0.3f,
            )
        }
    }
    val transition = rememberInfiniteTransition(label = "stars")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "t",
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        stars.forEach { star ->
            val alpha = (0.3f + 0.7f * (0.5f + 0.5f * sin(t * star.speed + star.phase))).coerceIn(0f, 1f)
            drawCircle(
                color = Color(0xFFE8F0FF).copy(alpha = alpha * 0.6f),
                radius = star.radius * density,
                center = Offset(star.x * size.width, star.y * size.height),
            )
        }
    }
}

private data class Star(val x: Float, val y: Float, val radius: Float, val phase: Float, val speed: Float)

@Composable
fun HudCard(
    modifier: Modifier = Modifier,
    glowColor: Color = JarvisCyan,
    onClick: (() -> Unit)? = null,
    contentPadding: Dp = 16.dp,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    val base = modifier
        .fillMaxWidth()
        .background(
            brush = Brush.verticalGradient(
                listOf(StarfieldCard.copy(alpha = 0.85f), DeepSpace.copy(alpha = 0.75f)),
            ),
            shape = shape,
        )
        .border(
            width = 1.dp,
            brush = Brush.linearGradient(listOf(glowColor.copy(alpha = 0.6f), NebulaEdge)),
            shape = shape,
        )
    val clickable = if (onClick != null) base.clickable(onClick = onClick) else base
    Box(modifier = clickable.padding(contentPadding)) {
        content()
    }
}

@Composable
fun HudPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val shape = RoundedCornerShape(12.dp)
    val alpha = if (enabled) 1f else 0.4f
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(brush = CosmicBrushes.goldGlow, shape = shape)
            .graphicsLayer { this.alpha = alpha }
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = CosmicBlack,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
        )
    }
}

@Composable
fun HudSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val shape = RoundedCornerShape(12.dp)
    val alpha = if (enabled) 1f else 0.4f
    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { this.alpha = alpha }
            .border(width = 1.dp, color = JarvisCyan.copy(alpha = 0.8f), shape = shape)
            .background(JarvisCyan.copy(alpha = 0.08f), shape = shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = JarvisCyan,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
fun HudScanningIndicator(
    modifier: Modifier = Modifier,
    label: String = "⚡ 小白師傅連線中",
) {
    val transition = rememberInfiniteTransition(label = "scan")
    val offsetX by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "scanX",
    )
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            color = JarvisCyan,
            style = MaterialTheme.typography.labelMedium,
        )
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .height(3.dp)
                .background(NebulaEdge.copy(alpha = 0.3f))
                .drawBehind {
                    val barWidth = size.width * 0.35f
                    val centerX = (size.width + barWidth) * ((offsetX + 1f) / 2f) - barWidth / 2f
                    drawRect(
                        brush = Brush.horizontalGradient(
                            listOf(Color.Transparent, JarvisCyan, Color.Transparent),
                        ),
                        topLeft = Offset(centerX - barWidth / 2f, 0f),
                        size = Size(barWidth, size.height),
                    )
                },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HudTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = JarvisCyan,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                ),
            )
        },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "返回",
                        tint = JarvisCyan,
                    )
                }
            }
        },
        actions = { actions() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = JarvisCyan,
            navigationIconContentColor = JarvisCyan,
            actionIconContentColor = IronmanGold,
        ),
    )
}
