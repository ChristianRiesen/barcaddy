package com.christianriesen.barcaddy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.christianriesen.barcaddy.ui.theme.BarcaddyTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardSheet(
    onClose: () -> Unit,
    onScan: () -> Unit,
    onManual: () -> Unit,
) {
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val close: (suspend () -> Unit) -> Unit = { after ->
        scope.launch {
            state.hide()
            after()
            onClose()
        }
    }
    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = state,
        containerColor = BarcaddyTheme.colors.bg,
    ) {
        SheetTitle("Add a card")
        SheetItem(Icons.Default.QrCodeScanner, "Scan barcode or QR", "Use camera") { close { onScan() } }
        SheetItem(Icons.Default.Edit, "Enter manually", "Type the code value") { close { onManual() } }
        Spacer(Modifier.height(12.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardActionsSheet(
    cardName: String,
    onClose: () -> Unit,
    onShow: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val close: (suspend () -> Unit) -> Unit = { after ->
        scope.launch {
            state.hide()
            after()
            onClose()
        }
    }
    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = state,
        containerColor = BarcaddyTheme.colors.bg,
    ) {
        SheetTitle(cardName)
        SheetItem(Icons.Default.Fullscreen, "Show fullscreen") { close { onShow() } }
        SheetItem(Icons.Default.Edit, "Edit") { close { onEdit() } }
        SheetItem(Icons.Default.Delete, "Delete", danger = true) { close { onDelete() } }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun SheetTitle(text: String) {
    Text(
        text,
        modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 4.dp, bottom = 12.dp),
        color = BarcaddyTheme.colors.ink,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun SheetItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    danger: Boolean = false,
    onClick: () -> Unit,
) {
    val c = BarcaddyTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(40.dp)
                .background(if (danger) c.dangerSoft else c.surfaceSoft, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = if (danger) c.danger else c.ink, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                title,
                color = if (danger) c.danger else c.ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
            if (subtitle != null) {
                Text(subtitle, color = c.muted, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun ConfirmDialog(
    title: String,
    body: String,
    confirmLabel: String,
    danger: Boolean = false,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(title, fontWeight = FontWeight.SemiBold) },
        text = { Text(body, color = BarcaddyTheme.colors.muted) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    confirmLabel,
                    color = if (danger) BarcaddyTheme.colors.danger else BarcaddyTheme.colors.accent,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancel", color = BarcaddyTheme.colors.ink) }
        },
        containerColor = BarcaddyTheme.colors.bg,
    )
}
