package com.chefai.toolbox.ui.calories

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chefai.toolbox.ChefAIApplication
import com.chefai.toolbox.ui.ChefViewModelFactory
import com.chefai.toolbox.ui.common.CosmicBackground
import com.chefai.toolbox.ui.common.HudCard
import com.chefai.toolbox.ui.common.HudPrimaryButton
import com.chefai.toolbox.ui.common.HudScanningIndicator
import com.chefai.toolbox.ui.common.HudSecondaryButton
import com.chefai.toolbox.ui.common.HudTopBar
import com.chefai.toolbox.ui.theme.ErrorRed
import com.chefai.toolbox.ui.theme.IceWhite
import com.chefai.toolbox.ui.theme.IronmanGold
import com.chefai.toolbox.ui.theme.JarvisCyan
import com.chefai.toolbox.ui.theme.NebulaEdge
import com.chefai.toolbox.ui.theme.NebulaPurple
import com.chefai.toolbox.ui.theme.StardustMuted
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CaloriesScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as ChefAIApplication
    val viewModel: CaloriesViewModel = viewModel(factory = ChefViewModelFactory(app.container))
    val state by viewModel.state.collectAsStateWithLifecycle()

    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    var pendingAction by remember { mutableStateOf<PendingAction?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
    ) { bitmap: Bitmap? ->
        if (bitmap != null) viewModel.setImage(bitmap)
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        if (uri != null) {
            val bitmap = loadBitmapFromUri(context, uri)
            if (bitmap != null) viewModel.setImage(bitmap)
        }
    }

    // 相機權限授權後自動觸發拍照
    LaunchedEffect(cameraPermission.status.isGranted, pendingAction) {
        if (pendingAction == PendingAction.TAKE_PHOTO && cameraPermission.status.isGranted) {
            takePictureLauncher.launch(null)
            pendingAction = null
        }
    }

    CosmicBackground {
        Scaffold(
            topBar = { HudTopBar(title = "熱量雷達", onBack = onBack) },
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

                ImagePreviewCard(
                    image = state.image,
                    onRetake = { viewModel.setImage(null) },
                )

                SourceButtonsCard(
                    onTakePhoto = {
                        if (cameraPermission.status.isGranted) {
                            takePictureLauncher.launch(null)
                        } else {
                            pendingAction = PendingAction.TAKE_PHOTO
                            cameraPermission.launchPermissionRequest()
                        }
                    },
                    onPickGallery = {
                        pickImageLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly,
                            ),
                        )
                    },
                )

                NoteInputCard(
                    value = state.note,
                    onChange = viewModel::setNote,
                    enabled = !state.isAnalyzing,
                )

                HudPrimaryButton(
                    text = if (state.isAnalyzing) "⚡ 小白師傅掃描中..." else "🔍 開始分析熱量",
                    onClick = viewModel::analyze,
                    enabled = state.image != null && !state.isAnalyzing,
                )

                if (state.isAnalyzing && state.output.isEmpty()) {
                    HudCard(glowColor = JarvisCyan) {
                        HudScanningIndicator(label = "⚡ Vision API 分析中，請稍候")
                    }
                }

                state.error?.let { err ->
                    HudCard(glowColor = ErrorRed) {
                        Column {
                            Text(
                                text = "⚠️ 分析失敗",
                                color = ErrorRed,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = err,
                                color = IceWhite,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }

                if (state.output.isNotEmpty()) {
                    AnalysisResultCard(
                        output = state.output,
                        isGenerating = state.isAnalyzing,
                        onReset = viewModel::reset,
                    )
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

private enum class PendingAction { TAKE_PHOTO }

@Composable
private fun IntroCard() {
    HudCard(glowColor = NebulaPurple) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "📸 拍照估算熱量",
                color = NebulaPurple,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = "對準已完成的餐點拍照或從相簿選取，" +
                    "小白師傅會估算熱量、三大營養素並提供飲食建議。",
                color = IceWhite,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "※ 誤差約 ±15%，僅供日常參考用。",
                color = StardustMuted,
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = "💰 使用旗艦模型 GPT-5.4，每張照片約 NT$0.30-0.50（精準度優先）",
                color = IronmanGold,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun ImagePreviewCard(image: Bitmap?, onRetake: () -> Unit) {
    HudCard(glowColor = JarvisCyan) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "餐點影像",
                color = JarvisCyan,
                style = MaterialTheme.typography.labelLarge,
            )
            if (image == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                        .background(NebulaEdge.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .border(1.dp, NebulaEdge, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = null,
                            tint = StardustMuted,
                            modifier = Modifier.size(48.dp),
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "尚未載入影像",
                            color = StardustMuted,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            } else {
                Image(
                    bitmap = image.asImageBitmap(),
                    contentDescription = "餐點照片",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                        .background(Color.Black, RoundedCornerShape(12.dp))
                        .border(1.dp, JarvisCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                )
                HudSecondaryButton(
                    text = "♻️ 重新選擇照片",
                    onClick = onRetake,
                )
            }
        }
    }
}

@Composable
private fun SourceButtonsCard(
    onTakePhoto: () -> Unit,
    onPickGallery: () -> Unit,
) {
    HudCard(glowColor = IronmanGold) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "影像來源",
                color = IronmanGold,
                style = MaterialTheme.typography.labelLarge,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SourceButton(
                    icon = Icons.Filled.CameraAlt,
                    label = "拍照",
                    onClick = onTakePhoto,
                    modifier = Modifier.weight(1f),
                )
                SourceButton(
                    icon = Icons.Filled.PhotoLibrary,
                    label = "從相簿",
                    onClick = onPickGallery,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun SourceButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .background(JarvisCyan.copy(alpha = 0.08f), shape)
            .border(1.dp, JarvisCyan.copy(alpha = 0.6f), shape)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = JarvisCyan,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                color = JarvisCyan,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun NoteInputCard(value: String, onChange: (String) -> Unit, enabled: Boolean) {
    HudCard(glowColor = NebulaPurple) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "備註（選填）",
                color = NebulaPurple,
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                text = "例如：這是午餐一人份、使用了較多油等，協助 AI 更準確估算。",
                color = StardustMuted,
                style = MaterialTheme.typography.bodySmall,
            )
            OutlinedTextField(
                value = value,
                onValueChange = onChange,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "輸入額外說明...",
                        color = StardustMuted,
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = IceWhite,
                    unfocusedTextColor = IceWhite,
                    disabledTextColor = StardustMuted,
                    focusedBorderColor = JarvisCyan,
                    unfocusedBorderColor = NebulaEdge,
                    cursorColor = JarvisCyan,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                ),
                minLines = 2,
                maxLines = 4,
            )
        }
    }
}

@Composable
private fun AnalysisResultCard(
    output: String,
    isGenerating: Boolean,
    onReset: () -> Unit,
) {
    HudCard(glowColor = IronmanGold) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "📊 分析結果",
                    color = IronmanGold,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    modifier = Modifier.weight(1f),
                )
                if (isGenerating) {
                    Text(
                        text = "串流中...",
                        color = JarvisCyan,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
            RenderCalorieText(output)
            if (!isGenerating) {
                HudSecondaryButton(
                    text = "♻️ 分析新的餐點",
                    onClick = onReset,
                )
            }
        }
    }
}

@Composable
private fun RenderCalorieText(text: String) {
    val lines = text.lines()
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        lines.forEach { raw ->
            val line = raw.trimEnd()
            when {
                line.isBlank() -> Spacer(Modifier.height(6.dp))
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
                line.startsWith("•") || line.startsWith("-") -> Text(
                    text = line,
                    color = IceWhite,
                    style = MaterialTheme.typography.bodyMedium,
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

private fun loadBitmapFromUri(context: android.content.Context, uri: Uri): Bitmap? {
    return try {
        val source = android.graphics.ImageDecoder.createSource(context.contentResolver, uri)
        android.graphics.ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
            decoder.isMutableRequired = false
            decoder.allocator = android.graphics.ImageDecoder.ALLOCATOR_SOFTWARE
        }
    } catch (t: Throwable) {
        try {
            @Suppress("DEPRECATION")
            android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } catch (_: Throwable) {
            null
        }
    }
}
