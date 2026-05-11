package com.christianriesen.barcaddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.christianriesen.barcaddy.data.Card
import com.christianriesen.barcaddy.ui.components.CardRow
import com.christianriesen.barcaddy.ui.components.IconBtn
import com.christianriesen.barcaddy.ui.theme.BarcaddyTheme

@Composable
fun HomeScreen(
    cards: List<Card>,
    onOpen: (String) -> Unit,
    onMore: (String) -> Unit,
    onAdd: () -> Unit,
    onSettings: () -> Unit,
    onReorder: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(cards, query) {
        if (query.isBlank()) cards
        else cards.filter {
            val q = query.lowercase()
            it.name.lowercase().contains(q) ||
                it.description.lowercase().contains(q) ||
                it.value.lowercase().contains(q)
        }
    }
    val c = BarcaddyTheme.colors

    Box(
        Modifier
            .fillMaxSize()
            .background(c.bg),
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 12.dp, top = 14.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BrandMark()
                Spacer(Modifier.weight(1f))
                IconBtn(Icons.AutoMirrored.Filled.List, onReorder, "Reorder")
                IconBtn(Icons.Default.Settings, onSettings, "Settings")
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .background(c.surfaceSoft, RoundedCornerShape(999.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.Search, null, tint = c.muted, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    cursorBrush = SolidColor(c.ink),
                    textStyle = TextStyle(color = c.ink, fontSize = 15.sp),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        if (query.isEmpty()) {
                            Text("Search cards", color = c.mutedSoft, fontSize = 15.sp)
                        }
                        inner()
                    },
                )
                if (query.isNotEmpty()) {
                    IconBtn(Icons.Default.Close, { query = "" }, "Clear", tint = c.muted, size = 24.dp)
                }
            }

            if (cards.isEmpty()) {
                EmptyState()
            } else if (filtered.isEmpty()) {
                NoMatches()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 6.dp, bottom = 110.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(filtered, key = { it.id }) { card ->
                        CardRow(
                            card = card,
                            onOpen = { onOpen(card.id) },
                            onMore = { onMore(card.id) },
                        )
                    }
                    item {
                        Spacer(Modifier.height(20.dp))
                        Text(
                            "${cards.size} cards",
                            color = c.mutedSoft,
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAdd,
            containerColor = c.ink,
            contentColor = c.bg,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 24.dp)
                .size(64.dp),
        ) {
            Icon(Icons.Default.Add, "Add card", modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
private fun BrandMark() {
    val c = BarcaddyTheme.colors
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(36.dp)
                .background(c.ink, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Bar(2.dp, 14.dp, c.accent)
                Bar(1.dp, 14.dp, Color.Transparent)
                Bar(1.dp, 14.dp, c.bg)
                Bar(1.dp, 14.dp, Color.Transparent)
                Bar(3.dp, 14.dp, c.bg)
                Bar(1.dp, 14.dp, Color.Transparent)
                Bar(1.dp, 14.dp, c.bg)
                Bar(1.dp, 14.dp, Color.Transparent)
                Bar(2.dp, 14.dp, c.bg)
            }
        }
        Spacer(Modifier.width(10.dp))
        Text(
            "Barcaddy",
            color = c.ink,
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.3).sp,
        )
    }
}

@Composable
private fun Bar(width: androidx.compose.ui.unit.Dp, height: androidx.compose.ui.unit.Dp, color: Color) {
    Box(Modifier.size(width, height).background(color))
}

@Composable
private fun EmptyState() {
    val c = BarcaddyTheme.colors
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("No cards yet", color = c.ink, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text(
            "Tap the plus button to scan a barcode or QR code, or enter one manually.",
            color = c.muted,
            fontSize = 14.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

@Composable
private fun NoMatches() {
    val c = BarcaddyTheme.colors
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("No matches", color = c.ink, fontSize = 22.sp)
        Spacer(Modifier.height(6.dp))
        Text("Try a different search.", color = c.muted, fontSize = 14.sp)
    }
}
