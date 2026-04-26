package com.chefai.toolbox.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.chefai.toolbox.ui.theme.CosmicBlack
import com.chefai.toolbox.ui.theme.ErrorRed
import com.chefai.toolbox.ui.theme.IceWhite
import com.chefai.toolbox.ui.theme.IronmanGold
import com.chefai.toolbox.ui.theme.JarvisCyan

/**
 * 全 App 共用的確認對話框：刪除/清除等不可逆動作必須先過這一關。
 * 預設為「危險動作」配色（金標題 + 紅色確認）。
 */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String = "確認",
    cancelText: String = "取消",
    confirmColor: androidx.compose.ui.graphics.Color = ErrorRed,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CosmicBlack,
        title = {
            Text(
                text = title,
                color = IronmanGold,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Text(
                text = message,
                color = IceWhite,
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmText, color = confirmColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = cancelText, color = JarvisCyan)
            }
        },
    )
}
