package com.chefai.toolbox.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chefai.toolbox.ui.theme.DeepSpace
import com.chefai.toolbox.ui.theme.IceWhite
import com.chefai.toolbox.ui.theme.IronmanGold
import com.chefai.toolbox.ui.theme.JarvisCyan
import com.chefai.toolbox.ui.theme.NebulaEdge
import com.chefai.toolbox.ui.theme.StardustMuted
import com.chefai.toolbox.ui.theme.StarfieldCard

@Composable
fun RecipePager(
    recipes: List<String>,
    modifier: Modifier = Modifier,
    topBanner: (@Composable () -> Unit)? = null,
) {
    val pagerState = rememberPagerState(pageCount = { recipes.size })
    Column(modifier = modifier) {
        topBanner?.invoke()
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            recipes.indices.forEach { i ->
                val active = pagerState.currentPage == i
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (active) 10.dp else 6.dp)
                        .background(
                            color = if (active) IronmanGold else NebulaEdge,
                            shape = CircleShape,
                        ),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            pageSpacing = 12.dp,
        ) { page ->
            RecipeCard(
                content = recipes[page],
                index = page + 1,
                total = recipes.size,
            )
        }
    }
}

@Composable
fun RecipeCard(
    content: String,
    index: Int,
    total: Int,
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    listOf(StarfieldCard, DeepSpace),
                ),
                shape = shape,
            )
            .border(1.dp, JarvisCyan.copy(alpha = 0.4f), shape)
            .padding(20.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                Text(
                    text = "第 $index / $total 道",
                    color = IronmanGold,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "← 左右滑動 →",
                    color = StardustMuted,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                RenderRecipeText(content)
            }
        }
    }
}

@Composable
fun RenderRecipeText(text: String) {
    val lines = text.lines()
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        var seenTitle = false
        var seenAnyStep = false
        lines.forEachIndexed { i, raw ->
            val line = raw.trimEnd()
            when {
                line.isBlank() -> Spacer(Modifier.height(4.dp))
                isTitleLine(line, lines, i) -> {
                    Text(
                        text = line,
                        color = IronmanGold,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                    )
                    Spacer(Modifier.height(8.dp))
                    seenTitle = true
                }
                line.startsWith("主料") || line.startsWith("調味") || line.startsWith("步驟") -> {
                    if (seenTitle) Spacer(Modifier.height(14.dp))
                    Text(
                        text = line,
                        color = JarvisCyan,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    Spacer(Modifier.height(4.dp))
                    if (line.startsWith("步驟")) seenAnyStep = false
                }
                line.startsWith("•") -> {
                    Text(
                        text = line,
                        color = IceWhite,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                isNumberedStep(line) -> {
                    if (seenAnyStep) Spacer(Modifier.height(10.dp))
                    Text(
                        text = line,
                        color = IceWhite,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    )
                    seenAnyStep = true
                }
                else -> {
                    Text(
                        text = line,
                        color = IceWhite,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

private val STEP_EMOJIS = setOf("1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣")

private fun isNumberedStep(line: String): Boolean = STEP_EMOJIS.any { line.startsWith(it) }

private fun isTitleLine(line: String, allLines: List<String>, index: Int): Boolean {
    if (index != 0 && allLines.take(index).any { it.isNotBlank() }) {
        if (allLines.subList(0, index).any { it.trim().isNotBlank() }) {
            return false
        }
    }
    return line.isNotBlank() && !line.startsWith("主料") &&
        !line.startsWith("調味") && !line.startsWith("步驟") &&
        !line.startsWith("•") && !isNumberedStep(line) &&
        line.length <= 20
}
