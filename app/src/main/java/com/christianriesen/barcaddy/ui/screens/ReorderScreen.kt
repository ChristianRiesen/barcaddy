package com.christianriesen.barcaddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.christianriesen.barcaddy.data.Card
import com.christianriesen.barcaddy.ui.components.IconBtn
import com.christianriesen.barcaddy.ui.theme.BarcaddyTheme
import com.christianriesen.barcaddy.ui.theme.Palettes

@Composable
fun ReorderScreen(
    initialCards: List<Card>,
    onCommit: (List<String>) -> Unit,
    onBack: () -> Unit,
) {
    val c = BarcaddyTheme.colors
    var working by remember(initialCards) { mutableStateOf(initialCards) }
    val density = LocalDensity.current
    val stridePx = with(density) { 66.dp.toPx() }
    var draggingId by remember { mutableStateOf<String?>(null) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    fun move(index: Int, dir: Int) {
        val target = index + dir
        if (target < 0 || target >= working.size) return
        val list = working.toMutableList()
        val tmp = list[index]
        list[index] = list[target]
        list[target] = tmp
        working = list
    }

    fun done() {
        onCommit(working.map { it.id })
        onBack()
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(c.bg),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconBtn(Icons.AutoMirrored.Filled.ArrowBack, ::done, "Back")
            Text(
                "Reorder",
                modifier = Modifier.weight(1f),
                color = c.ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            Text(
                "Done",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable(onClick = ::done)
                    .padding(8.dp),
                color = c.accent,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Text(
            "Drag the handle or tap the arrows to reorder. Tap Done to save.",
            color = c.muted,
            fontSize = 13.sp,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 8.dp),
        )
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(working, key = { _, card -> card.id }) { idx, card ->
                val isDragging = draggingId == card.id
                ReorderRow(
                    card = card,
                    isFirst = idx == 0,
                    isLast = idx == working.size - 1,
                    isDragging = isDragging,
                    onUp = { move(idx, -1) },
                    onDown = { move(idx, 1) },
                    onDragStart = {
                        draggingId = card.id
                        dragOffsetY = 0f
                    },
                    onDrag = { delta ->
                        val total = dragOffsetY + delta
                        when {
                            total > stridePx / 2 && idx < working.size - 1 -> {
                                working = working.toMutableList().apply {
                                    val moved = removeAt(idx)
                                    add(idx + 1, moved)
                                }
                                dragOffsetY = total - stridePx
                            }
                            total < -stridePx / 2 && idx > 0 -> {
                                working = working.toMutableList().apply {
                                    val moved = removeAt(idx)
                                    add(idx - 1, moved)
                                }
                                dragOffsetY = total + stridePx
                            }
                            else -> dragOffsetY = total
                        }
                    },
                    onDragEnd = {
                        draggingId = null
                        dragOffsetY = 0f
                    },
                    rowModifier = if (isDragging) {
                        Modifier
                            .zIndex(1f)
                            .graphicsLayer { translationY = dragOffsetY }
                    } else {
                        Modifier.animateItem()
                    },
                )
            }
        }
    }
}

@Composable
private fun LazyItemScope.ReorderRow(
    card: Card,
    isFirst: Boolean,
    isLast: Boolean,
    isDragging: Boolean,
    onUp: () -> Unit,
    onDown: () -> Unit,
    onDragStart: () -> Unit,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit,
    rowModifier: Modifier,
) {
    val latestOnDragStart by rememberUpdatedState(onDragStart)
    val latestOnDrag by rememberUpdatedState(onDrag)
    val latestOnDragEnd by rememberUpdatedState(onDragEnd)
    val c = BarcaddyTheme.colors
    val palette = Palettes.byName(card.palette)
    Row(
        rowModifier
            .fillMaxWidth()
            .background(c.surface, RoundedCornerShape(14.dp))
            .padding(start = 4.dp, end = 4.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(36.dp)
                .pointerInput(card.id) {
                    detectDragGestures(
                        onDragStart = { latestOnDragStart() },
                        onDragEnd = { latestOnDragEnd() },
                        onDragCancel = { latestOnDragEnd() },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            latestOnDrag(dragAmount.y)
                        },
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Default.DragIndicator,
                "Drag to reorder",
                tint = if (isDragging) c.ink else c.mutedSoft,
                modifier = Modifier.size(22.dp),
            )
        }
        Box(
            Modifier
                .size(38.dp)
                .background(palette.bg, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(card.monogram, color = palette.deep, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(
                card.name,
                color = c.ink,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (card.description.isNotBlank()) {
                Text(
                    card.description,
                    color = c.mutedSoft,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        ArrowButton(Icons.Default.KeyboardArrowUp, "Move up", isFirst, onUp)
        ArrowButton(Icons.Default.KeyboardArrowDown, "Move down", isLast, onDown)
    }
}

@Composable
private fun ArrowButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    disabled: Boolean,
    onClick: () -> Unit,
) {
    val c = BarcaddyTheme.colors
    Box(
        Modifier
            .size(34.dp)
            .clickable(enabled = !disabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription,
            tint = if (disabled) c.mutedSoft.copy(alpha = 0.4f) else c.ink,
        )
    }
}
