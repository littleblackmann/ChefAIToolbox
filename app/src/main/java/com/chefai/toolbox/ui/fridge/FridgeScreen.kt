package com.chefai.toolbox.ui.fridge

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
import com.chefai.toolbox.ui.theme.CosmicBlack
import com.chefai.toolbox.ui.theme.ErrorRed
import com.chefai.toolbox.ui.theme.IceWhite
import com.chefai.toolbox.ui.theme.IronmanGold
import com.chefai.toolbox.ui.theme.JarvisCyan
import com.chefai.toolbox.ui.theme.NebulaEdge
import com.chefai.toolbox.ui.theme.NebulaPurple
import com.chefai.toolbox.ui.theme.StardustMuted

@Composable
fun FridgeScreen(onBack: () -> Unit) {
    val app = LocalContext.current.applicationContext as ChefAIApplication
    val viewModel: FridgeViewModel = viewModel(factory = ChefViewModelFactory(app.container))
    val state by viewModel.state.collectAsStateWithLifecycle()

    CosmicBackground {
        Scaffold(
            topBar = {
                HudTopBar(
                    title = "冰箱食譜生成",
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
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                SelectedIngredientsBar(
                    selected = state.selectedIngredients,
                    onRemove = viewModel::removeIngredient,
                    onClearAll = viewModel::clearAllIngredients,
                )
                CategoryTabs(
                    selected = state.selectedCategory,
                    onSelect = viewModel::selectCategory,
                )
                if (state.selectedCategory == IngredientCategory.OTHER) {
                    OtherInputCard(
                        input = state.customIngredientInput,
                        onInputChange = viewModel::onCustomInputChange,
                        onAdd = viewModel::addCustomIngredient,
                    )
                } else {
                    IngredientGrid(
                        category = state.selectedCategory,
                        selected = state.selectedIngredients,
                        onToggle = viewModel::toggleIngredient,
                    )
                }
                StyleSelector(
                    current = state.style,
                    onSelect = viewModel::setStyle,
                )
                ServingsSelector(
                    servings = state.servings,
                    onChange = viewModel::setServings,
                )
                RecipeCountSelector(
                    count = state.recipeCount,
                    onChange = viewModel::setRecipeCount,
                )
                HudPrimaryButton(
                    text = "🚀 生成 ${state.recipeCount} 道食譜",
                    onClick = viewModel::generate,
                    enabled = state.selectedIngredients.isNotEmpty() && !state.isGenerating,
                )
                Spacer(Modifier.height(12.dp))
            }
        }

        if (state.showResult) {
            RecipeResultDialog(
                state = state,
                onClose = viewModel::closeResult,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SelectedIngredientsBar(
    selected: Set<String>,
    onRemove: (String) -> Unit,
    onClearAll: () -> Unit,
) {
    HudCard(glowColor = IronmanGold) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "已選食材 (${selected.size})",
                    color = IronmanGold,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.weight(1f),
                )
                if (selected.isNotEmpty()) {
                    Text(
                        text = "清除",
                        color = StardustMuted,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.clickable(onClick = onClearAll),
                    )
                }
            }
            if (selected.isEmpty()) {
                Text(
                    text = "從下方分類中選擇食材",
                    color = StardustMuted,
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    selected.forEach { name ->
                        Row(
                            modifier = Modifier
                                .background(JarvisCyan.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .border(1.dp, JarvisCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable { onRemove(name) }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = name,
                                color = JarvisCyan,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Spacer(Modifier.size(4.dp))
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "移除",
                                tint = JarvisCyan,
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryTabs(
    selected: IngredientCategory,
    onSelect: (IngredientCategory) -> Unit,
) {
    val all = IngredientCategory.entries
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        all.forEach { cat ->
            CategoryTab(
                category = cat,
                selected = cat == selected,
                onClick = { onSelect(cat) },
            )
        }
    }
}

@Composable
private fun CategoryTab(
    category: IngredientCategory,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) JarvisCyan else NebulaEdge
    val bgColor = if (selected) JarvisCyan.copy(alpha = 0.15f) else Color.Transparent
    val textColor = if (selected) JarvisCyan else StardustMuted
    Row(
        modifier = Modifier
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .background(bgColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(category.emoji, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.size(6.dp))
        Text(
            text = category.label,
            color = textColor,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IngredientGrid(
    category: IngredientCategory,
    selected: Set<String>,
    onToggle: (String) -> Unit,
) {
    val group = IngredientCatalog.groups.firstOrNull { it.category == category } ?: return
    HudCard(glowColor = NebulaPurple) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            group.items.forEach { name ->
                IngredientChip(
                    name = name,
                    selected = selected.contains(name),
                    onClick = { onToggle(name) },
                )
            }
        }
    }
}

@Composable
private fun IngredientChip(
    name: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) JarvisCyan else NebulaEdge
    val bgColor = if (selected) JarvisCyan.copy(alpha = 0.2f) else CosmicBlack.copy(alpha = 0.3f)
    val textColor = if (selected) JarvisCyan else IceWhite
    Box(
        modifier = Modifier
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .background(bgColor, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = name,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            ),
        )
    }
}

@Composable
private fun OtherInputCard(
    input: String,
    onInputChange: (String) -> Unit,
    onAdd: () -> Unit,
) {
    HudCard(glowColor = NebulaPurple) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "輸入其他食材",
                color = NebulaPurple,
                style = MaterialTheme.typography.labelLarge,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = onInputChange,
                    placeholder = { Text("例如：金針菇、杏鮑菇", color = StardustMuted) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onAdd() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = IceWhite,
                        unfocusedTextColor = IceWhite,
                        focusedBorderColor = JarvisCyan,
                        unfocusedBorderColor = StardustMuted,
                        cursorColor = JarvisCyan,
                    ),
                )
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(JarvisCyan, CircleShape)
                        .clickable(onClick = onAdd),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "加入", tint = CosmicBlack)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StyleSelector(
    current: CuisineStyle,
    onSelect: (CuisineStyle) -> Unit,
) {
    HudCard(glowColor = IronmanGold) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "料理風格",
                color = IronmanGold,
                style = MaterialTheme.typography.labelLarge,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CuisineStyle.entries.forEach { style ->
                    StyleChip(
                        style = style,
                        selected = current == style,
                        onClick = { onSelect(style) },
                    )
                }
            }
        }
    }
}

@Composable
private fun StyleChip(
    style: CuisineStyle,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) IronmanGold else NebulaEdge
    val bgColor = if (selected) IronmanGold.copy(alpha = 0.2f) else Color.Transparent
    val textColor = if (selected) IronmanGold else IceWhite
    Row(
        modifier = Modifier
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .background(bgColor, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(style.emoji, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.size(6.dp))
        Text(
            text = style.label,
            color = textColor,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun RecipeCountSelector(
    count: Int,
    onChange: (Int) -> Unit,
) {
    HudCard(glowColor = NebulaPurple) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "生成份數",
                color = NebulaPurple,
                style = MaterialTheme.typography.labelLarge,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                (1..3).forEach { n ->
                    val selected = n == count
                    val borderColor = if (selected) NebulaPurple else NebulaEdge
                    val bgColor = if (selected) NebulaPurple.copy(alpha = 0.2f) else Color.Transparent
                    val textColor = if (selected) NebulaPurple else IceWhite
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                            .background(bgColor, RoundedCornerShape(10.dp))
                            .clickable { onChange(n) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "$n 道",
                            color = textColor,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ServingsSelector(
    servings: Int,
    onChange: (Int) -> Unit,
) {
    HudCard(glowColor = JarvisCyan) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "幾人份",
                color = JarvisCyan,
                style = MaterialTheme.typography.labelLarge,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                (1..4).forEach { n ->
                    ServingsButton(
                        label = "${n}人",
                        selected = n == servings,
                        onClick = { onChange(n) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                (5..7).forEach { n ->
                    ServingsButton(
                        label = "${n}人",
                        selected = n == servings,
                        onClick = { onChange(n) },
                        modifier = Modifier.weight(1f),
                    )
                }
                ServingsButton(
                    label = "8人+",
                    selected = servings == 8,
                    onClick = { onChange(8) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ServingsButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (selected) JarvisCyan else NebulaEdge
    val bgColor = if (selected) JarvisCyan.copy(alpha = 0.2f) else Color.Transparent
    val textColor = if (selected) JarvisCyan else IceWhite
    Box(
        modifier = modifier
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .background(bgColor, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        )
    }
}
