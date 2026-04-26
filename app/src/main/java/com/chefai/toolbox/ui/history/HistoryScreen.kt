package com.chefai.toolbox.ui.history

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chefai.toolbox.ChefAIApplication
import com.chefai.toolbox.data.CalorieRecord
import com.chefai.toolbox.data.RecipeRecord
import com.chefai.toolbox.ui.ChefViewModelFactory
import com.chefai.toolbox.ui.common.ConfirmDialog
import com.chefai.toolbox.ui.common.CosmicBackground
import com.chefai.toolbox.ui.common.HudCard
import com.chefai.toolbox.ui.common.HudTopBar
import com.chefai.toolbox.ui.common.RecipePager
import com.chefai.toolbox.ui.theme.CosmicBlack
import com.chefai.toolbox.ui.theme.ErrorRed
import com.chefai.toolbox.ui.theme.IceWhite
import com.chefai.toolbox.ui.theme.IronmanGold
import com.chefai.toolbox.ui.theme.JarvisCyan
import com.chefai.toolbox.ui.theme.NebulaEdge
import com.chefai.toolbox.ui.theme.NebulaPurple
import com.chefai.toolbox.ui.theme.StardustMuted
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(onBack: () -> Unit) {
    var tabIndex by rememberSaveable { mutableIntStateOf(0) }

    CosmicBackground {
        Scaffold(
            topBar = { HudTopBar(title = "我的紀錄", onBack = onBack) },
            containerColor = Color.Transparent,
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                TabRow(
                    selectedTabIndex = tabIndex,
                    containerColor = Color.Transparent,
                    contentColor = IronmanGold,
                ) {
                    Tab(
                        selected = tabIndex == 0,
                        onClick = { tabIndex = 0 },
                        text = {
                            Text(
                                text = "🗄️ 料理",
                                fontWeight = if (tabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (tabIndex == 0) IronmanGold else StardustMuted,
                            )
                        },
                    )
                    Tab(
                        selected = tabIndex == 1,
                        onClick = { tabIndex = 1 },
                        text = {
                            Text(
                                text = "📊 熱量",
                                fontWeight = if (tabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (tabIndex == 1) IronmanGold else StardustMuted,
                            )
                        },
                    )
                }
                when (tabIndex) {
                    0 -> RecipeHistoryTab()
                    else -> CalorieHistoryTab()
                }
            }
        }
    }
}

// =============================================================================
// 料理紀錄 Tab
// =============================================================================

@Composable
private fun RecipeHistoryTab() {
    val app = androidx.compose.ui.platform.LocalContext.current.applicationContext as ChefAIApplication
    val viewModel: HistoryViewModel = viewModel(factory = ChefViewModelFactory(app.container))
    val records by viewModel.records.collectAsStateWithLifecycle()
    val ui by viewModel.ui.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        SelectionActionBar(
            visible = records.isNotEmpty(),
            selectionMode = ui.selectionMode,
            checkedCount = ui.checkedIds.size,
            allCount = records.size,
            onEnterSelection = { viewModel.enterSelection() },
            onExitSelection = viewModel::exitSelection,
            onSelectAll = viewModel::selectAll,
            onAskBatchDelete = viewModel::askBatchDelete,
            onAskClearAll = viewModel::askClear,
        )
        if (records.isEmpty()) {
            EmptyState(
                modifier = Modifier.fillMaxSize(),
                title = "還沒有任何料理紀錄",
                hint = "每次冰箱食譜生成成功後會自動儲存到這裡",
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(records.size) { index ->
                    val record = records[index]
                    RecipeRecordCard(
                        record = record,
                        selectionMode = ui.selectionMode,
                        checked = record.id in ui.checkedIds,
                        onClick = { viewModel.open(record) },
                        onLongClick = { viewModel.enterSelection(record.id) },
                        onAskDelete = { viewModel.askDelete(record.id) },
                    )
                }
            }
        }
    }

    ui.selectedRecord?.let { record ->
        RecipeDetailDialog(record = record, onClose = viewModel::closeDetail)
    }

    ui.pendingDeleteId?.let {
        ConfirmDialog(
            title = "刪除這筆紀錄？",
            message = "刪除後無法復原。",
            confirmText = "刪除",
            onConfirm = viewModel::confirmDelete,
            onDismiss = viewModel::dismissDeleteDialog,
        )
    }

    if (ui.showBatchDeleteDialog) {
        ConfirmDialog(
            title = "刪除已選的 ${ui.checkedIds.size} 筆紀錄？",
            message = "這些料理紀錄會永久移除，無法復原。",
            confirmText = "刪除",
            onConfirm = viewModel::confirmBatchDelete,
            onDismiss = viewModel::dismissBatchDeleteDialog,
        )
    }

    if (ui.showClearDialog) {
        ConfirmDialog(
            title = "清除全部料理紀錄？",
            message = "這會永久移除所有料理紀錄，無法復原。",
            confirmText = "全部清除",
            onConfirm = viewModel::confirmClear,
            onDismiss = viewModel::dismissClearDialog,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RecipeRecordCard(
    record: RecipeRecord,
    selectionMode: Boolean,
    checked: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onAskDelete: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            ),
    ) {
        HudCard(
            glowColor = if (checked) IronmanGold else JarvisCyan,
            contentPadding = 14.dp,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (selectionMode) {
                    Icon(
                        imageVector = if (checked) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = if (checked) "已選擇" else "未選擇",
                        tint = if (checked) IronmanGold else StardustMuted,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(24.dp),
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🍽️ ${record.style}風",
                            color = IronmanGold,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        )
                        record.servingsLabel()?.let { label ->
                            Spacer(Modifier.size(8.dp))
                            Text(
                                text = "👥 $label",
                                color = JarvisCyan,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                modifier = Modifier
                                    .background(JarvisCyan.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                    .border(1.dp, JarvisCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = formatTime(record.timestamp),
                            color = StardustMuted,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                    val titles = record.previewTitles()
                    if (titles.isNotEmpty()) {
                        Text(
                            text = titles.joinToString("・"),
                            color = IceWhite,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        )
                    }
                    if (record.ingredients.isNotEmpty()) {
                        Text(
                            text = "食材：" + record.ingredients.joinToString("、"),
                            color = StardustMuted,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Text(
                        text = "共 ${record.recipes.size} 道・點擊查看詳細",
                        color = NebulaPurple,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                if (!selectionMode) {
                    IconButton(onClick = onAskDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "刪除",
                            tint = StardustMuted,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeDetailDialog(record: RecipeRecord, onClose: () -> Unit) {
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
                .background(CosmicBlack.copy(alpha = 0.96f)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 40.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "⚡ ${record.style}風料理",
                            color = JarvisCyan,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = formatFullTime(record.timestamp),
                                color = StardustMuted,
                                style = MaterialTheme.typography.labelMedium,
                            )
                            record.servingsLabel()?.let { label ->
                                Spacer(Modifier.size(10.dp))
                                Text(
                                    text = "👥 $label",
                                    color = IronmanGold,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                )
                            }
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
                Spacer(Modifier.height(8.dp))

                RecipePager(
                    recipes = record.recipes,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

// =============================================================================
// 熱量紀錄 Tab
// =============================================================================

@Composable
private fun CalorieHistoryTab() {
    val app = androidx.compose.ui.platform.LocalContext.current.applicationContext as ChefAIApplication
    val viewModel: CalorieHistoryViewModel = viewModel(factory = ChefViewModelFactory(app.container))
    val records by viewModel.records.collectAsStateWithLifecycle()
    val ui by viewModel.ui.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        SelectionActionBar(
            visible = records.isNotEmpty(),
            selectionMode = ui.selectionMode,
            checkedCount = ui.checkedIds.size,
            allCount = records.size,
            onEnterSelection = { viewModel.enterSelection() },
            onExitSelection = viewModel::exitSelection,
            onSelectAll = viewModel::selectAll,
            onAskBatchDelete = viewModel::askBatchDelete,
            onAskClearAll = viewModel::askClear,
        )
        if (records.isEmpty()) {
            EmptyState(
                modifier = Modifier.fillMaxSize(),
                title = "還沒有任何熱量紀錄",
                hint = "每次熱量雷達分析完成後會自動儲存到這裡",
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(records.size) { index ->
                    val record = records[index]
                    CalorieRecordCard(
                        record = record,
                        selectionMode = ui.selectionMode,
                        checked = record.id in ui.checkedIds,
                        onClick = { viewModel.open(record) },
                        onLongClick = { viewModel.enterSelection(record.id) },
                        onAskDelete = { viewModel.askDelete(record.id) },
                    )
                }
            }
        }
    }

    ui.selectedRecord?.let { record ->
        CalorieDetailDialog(record = record, onClose = viewModel::closeDetail)
    }

    ui.pendingDeleteId?.let {
        ConfirmDialog(
            title = "刪除這筆紀錄？",
            message = "照片與分析結果都會被永久刪除，無法復原。",
            confirmText = "刪除",
            onConfirm = viewModel::confirmDelete,
            onDismiss = viewModel::dismissDeleteDialog,
        )
    }

    if (ui.showBatchDeleteDialog) {
        ConfirmDialog(
            title = "刪除已選的 ${ui.checkedIds.size} 筆紀錄？",
            message = "這些熱量紀錄與照片都會永久移除，無法復原。",
            confirmText = "刪除",
            onConfirm = viewModel::confirmBatchDelete,
            onDismiss = viewModel::dismissBatchDeleteDialog,
        )
    }

    if (ui.showClearDialog) {
        ConfirmDialog(
            title = "清除全部熱量紀錄？",
            message = "這會永久移除所有熱量紀錄與照片，無法復原。",
            confirmText = "全部清除",
            onConfirm = viewModel::confirmClear,
            onDismiss = viewModel::dismissClearDialog,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CalorieRecordCard(
    record: CalorieRecord,
    selectionMode: Boolean,
    checked: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onAskDelete: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            ),
    ) {
        HudCard(
            glowColor = if (checked) IronmanGold else NebulaPurple,
            contentPadding = 12.dp,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (selectionMode) {
                    Icon(
                        imageVector = if (checked) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = if (checked) "已選擇" else "未選擇",
                        tint = if (checked) IronmanGold else StardustMuted,
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(24.dp),
                    )
                }
                Thumbnail(
                    path = record.imagePath,
                    modifier = Modifier
                        .size(72.dp)
                        .background(NebulaEdge.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .border(1.dp, JarvisCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                )
                Spacer(Modifier.size(12.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = record.dishName.ifBlank { "未命名料理" },
                        color = IronmanGold,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    if (record.caloriesText.isNotBlank()) {
                        Text(
                            text = "🔥 ${record.caloriesText}",
                            color = ErrorRed,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        )
                    }
                    Text(
                        text = formatFullTime(record.timestamp),
                        color = StardustMuted,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                if (!selectionMode) {
                    IconButton(onClick = onAskDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "刪除",
                            tint = StardustMuted,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Thumbnail(path: String, modifier: Modifier = Modifier) {
    val bitmap = remember(path) {
        try {
            val f = File(path)
            if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
        } catch (_: Throwable) {
            null
        }
    }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "餐點縮圖",
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black, RoundedCornerShape(10.dp)),
            )
        } else {
            Text(text = "🖼️", color = StardustMuted)
        }
    }
}

@Composable
private fun CalorieDetailDialog(record: CalorieRecord, onClose: () -> Unit) {
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
                .background(CosmicBlack.copy(alpha = 0.96f)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 40.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "📊 ${record.dishName.ifBlank { "未命名料理" }}",
                            color = JarvisCyan,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                        )
                        Text(
                            text = formatFullTime(record.timestamp),
                            color = StardustMuted,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "關閉",
                            tint = IceWhite,
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    DetailPhoto(record.imagePath)
                    HudCard(glowColor = IronmanGold) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            record.rawOutput.lines().forEach { raw ->
                                val line = raw.trimEnd()
                                when {
                                    line.isBlank() -> Spacer(Modifier.height(4.dp))
                                    line.startsWith("📸") -> Text(
                                        text = line,
                                        color = IronmanGold,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    )
                                    line.startsWith("🔥") -> Text(
                                        text = line,
                                        color = ErrorRed,
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                    )
                                    line.startsWith("📊") -> Text(
                                        text = line,
                                        color = JarvisCyan,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    )
                                    line.startsWith("💡") -> Text(
                                        text = line,
                                        color = NebulaPurple,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    )
                                    line.startsWith("⚠️") -> Text(
                                        text = line,
                                        color = StardustMuted,
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                    else -> Text(
                                        text = line,
                                        color = IceWhite,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailPhoto(path: String) {
    val bitmap = remember(path) {
        try {
            val f = File(path)
            if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
        } catch (_: Throwable) {
            null
        }
    }
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "餐點照片",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .background(Color.Black, RoundedCornerShape(12.dp))
                .border(1.dp, JarvisCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .background(NebulaEdge.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                .border(1.dp, NebulaEdge, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "（照片已遺失）",
                color = StardustMuted,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

// =============================================================================
// 共用元件
// =============================================================================

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
    title: String,
    hint: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Inbox,
            contentDescription = null,
            tint = StardustMuted,
            modifier = Modifier.size(64.dp),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = title,
            color = StardustMuted,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = hint,
            color = StardustMuted,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun SelectionActionBar(
    visible: Boolean,
    selectionMode: Boolean,
    checkedCount: Int,
    allCount: Int,
    onEnterSelection: () -> Unit,
    onExitSelection: () -> Unit,
    onSelectAll: () -> Unit,
    onAskBatchDelete: () -> Unit,
    onAskClearAll: () -> Unit,
) {
    if (!visible) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (selectionMode) {
            Text(
                text = "已選 $checkedCount / $allCount",
                color = IronmanGold,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f),
            )
            ActionPill(label = "全選", tint = JarvisCyan, onClick = onSelectAll)
            Spacer(Modifier.size(8.dp))
            ActionPill(
                icon = Icons.Filled.DeleteOutline,
                label = "刪除",
                tint = ErrorRed,
                onClick = onAskBatchDelete,
            )
            Spacer(Modifier.size(8.dp))
            ActionPill(
                icon = Icons.Filled.Close,
                label = "取消",
                tint = StardustMuted,
                onClick = onExitSelection,
            )
        } else {
            Text(
                text = "共 $allCount 筆",
                color = StardustMuted,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.weight(1f),
            )
            ActionPill(
                icon = Icons.Filled.Check,
                label = "選擇",
                tint = JarvisCyan,
                onClick = onEnterSelection,
            )
            Spacer(Modifier.size(8.dp))
            ActionPill(
                icon = Icons.Filled.DeleteSweep,
                label = "全部清除",
                tint = ErrorRed,
                onClick = onAskClearAll,
            )
        }
    }
}

@Composable
private fun ActionPill(
    label: String,
    tint: Color,
    onClick: () -> Unit,
    icon: ImageVector? = null,
) {
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .background(tint.copy(alpha = 0.08f), shape)
            .border(1.dp, tint.copy(alpha = 0.6f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(18.dp),
            )
        }
        Text(
            text = label,
            color = tint,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
        )
    }
}

private fun formatTime(ts: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - ts
    return when {
        diff < 60_000L -> "剛剛"
        diff < 3_600_000L -> "${diff / 60_000L} 分鐘前"
        diff < 86_400_000L -> "${diff / 3_600_000L} 小時前"
        diff < 7 * 86_400_000L -> "${diff / 86_400_000L} 天前"
        else -> SimpleDateFormat("MM/dd", Locale.getDefault()).format(Date(ts))
    }
}

private fun formatFullTime(ts: Long): String =
    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(ts))
