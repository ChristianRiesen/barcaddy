package com.christianriesen.barcaddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.christianriesen.barcaddy.data.Card
import com.christianriesen.barcaddy.data.CodeKind
import com.christianriesen.barcaddy.ui.components.BarcaddyFormat
import com.christianriesen.barcaddy.ui.components.IconBtn
import com.christianriesen.barcaddy.ui.components.PaletteSwatch
import com.christianriesen.barcaddy.ui.theme.BarcaddyTheme
import com.christianriesen.barcaddy.ui.theme.Palettes

@Composable
fun FormScreen(
    existing: Card?,
    seedValue: String? = null,
    seedFormat: BarcaddyFormat? = null,
    suggestions: List<String>,
    onCancel: () -> Unit,
    onSave: (Card) -> Unit,
    onDelete: (String) -> Unit,
) {
    val c = BarcaddyTheme.colors
    val isEditing = existing != null

    var name by rememberSaveable { mutableStateOf(existing?.name ?: "") }
    var value by rememberSaveable { mutableStateOf(existing?.value ?: seedValue ?: "") }
    var description by rememberSaveable { mutableStateOf(existing?.description ?: "") }
    var palette by rememberSaveable {
        mutableStateOf(existing?.palette ?: Palettes.random().name)
    }
    var format by rememberSaveable {
        mutableStateOf(
            existing?.let { BarcaddyFormat.forName(it.format) }
                ?: seedFormat
                ?: BarcaddyFormat.CODE_128
        )
    }
    var kind by rememberSaveable { mutableStateOf(format.kind) }

    // Keep format in sync when kind switches (e.g. user toggles QR/Barcode)
    if (format.kind != kind) {
        format = if (kind == CodeKind.BARCODE) BarcaddyFormat.CODE_128 else BarcaddyFormat.QR_CODE
    }

    val monogram = remember(name) {
        val pieces = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        if (pieces.isEmpty()) "NC"
        else pieces.take(2).joinToString("") { it.first().toString() }.uppercase()
    }
    val canSave = name.isNotBlank() && value.isNotBlank()
    val scroll = rememberScrollState()

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
            IconBtn(Icons.AutoMirrored.Filled.ArrowBack, onCancel, "Back")
            Text(
                if (isEditing) "Edit card" else "New card",
                modifier = Modifier.weight(1f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = c.ink,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            Text(
                "Save",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable(enabled = canSave) {
                        if (canSave) {
                            onSave(
                                Card(
                                    id = existing?.id ?: ("c_" + java.util.UUID.randomUUID().toString().take(8)),
                                    name = name.trim(),
                                    value = value.trim(),
                                    kind = kind,
                                    format = format.name,
                                    description = description.trim(),
                                    palette = palette,
                                    monogram = monogram,
                                    position = existing?.position ?: 0,
                                ),
                            )
                        }
                    }
                    .padding(8.dp),
                color = if (canSave) c.accent else c.mutedSoft,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            )
        }

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(scroll)
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(8.dp))
            Field("Name") {
                TextInput(value = name, onChange = { name = it }, placeholder = "e.g. Whole Foods")
            }

            Field("Type") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Segment("Barcode", kind == CodeKind.BARCODE, Modifier.weight(1f)) { kind = CodeKind.BARCODE }
                    Segment("QR / 2D", kind == CodeKind.QR, Modifier.weight(1f)) { kind = CodeKind.QR }
                }
            }

            Field("Format") {
                FormatDropdown(kind = kind, value = format, onChange = { format = it })
            }

            Field("Code value") {
                TextInput(
                    value = value,
                    onChange = { value = it },
                    placeholder = if (kind == CodeKind.QR) "e.g. https://example.com or any text"
                                  else "e.g. 4012345678901",
                )
            }

            Field("Description (optional)") {
                TextInput(
                    value = description,
                    onChange = { description = it },
                    placeholder = "e.g. Loyalty card, Coupon, Membership",
                )
                if (suggestions.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    ChipsRow(suggestions, description) { description = if (description == it) "" else it }
                }
            }

            Field("Color") {
                FlowGrid(items = Palettes.all.map { it.name }, columns = 5) { paletteName ->
                    PaletteSwatch(
                        paletteName = paletteName,
                        monogram = monogram,
                        selected = palette == paletteName,
                        onClick = { palette = paletteName },
                    )
                }
            }

            if (isEditing) {
                Spacer(Modifier.height(20.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(c.divider),
                )
                Row(
                    Modifier
                        .padding(vertical = 16.dp)
                        .clickable { existing?.let { onDelete(it.id) } },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.Delete, null, tint = c.danger, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Delete card", color = c.danger, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun Field(label: String, content: @Composable () -> Unit) {
    val c = BarcaddyTheme.colors
    Column(Modifier.padding(top = 4.dp, bottom = 14.dp)) {
        Text(
            label.uppercase(),
            color = c.muted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.0.sp,
            modifier = Modifier.padding(start = 2.dp, bottom = 8.dp),
        )
        content()
    }
}

@Composable
private fun TextInput(value: String, onChange: (String) -> Unit, placeholder: String) {
    val c = BarcaddyTheme.colors
    Box(
        Modifier
            .fillMaxWidth()
            .background(c.surface, RoundedCornerShape(14.dp))
            .border(1.5.dp, c.divider, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            cursorBrush = SolidColor(c.ink),
            textStyle = TextStyle(color = c.ink, fontSize = 15.sp),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { inner ->
                if (value.isEmpty()) Text(placeholder, color = c.mutedSoft, fontSize = 15.sp)
                inner()
            },
        )
    }
}

@Composable
private fun Segment(label: String, active: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val c = BarcaddyTheme.colors
    Box(
        modifier
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (active) c.ink else c.surface)
            .border(if (active) 0.dp else 1.5.dp, c.divider, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            color = if (active) c.bg else c.ink,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun FormatDropdown(kind: CodeKind, value: BarcaddyFormat, onChange: (BarcaddyFormat) -> Unit) {
    val c = BarcaddyTheme.colors
    val options = if (kind == CodeKind.BARCODE) BarcaddyFormat.barcodeFormats() else BarcaddyFormat.qrFormats()
    var expanded by remember { mutableStateOf(false) }
    Box {
        Row(
            Modifier
                .fillMaxWidth()
                .background(c.surface, RoundedCornerShape(14.dp))
                .border(1.5.dp, c.divider, RoundedCornerShape(14.dp))
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(value.displayName, color = c.ink, fontSize = 15.sp, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ArrowDropDown, null, tint = c.muted)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { f ->
                DropdownMenuItem(
                    text = { Text(f.displayName) },
                    onClick = {
                        onChange(f)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun ChipsRow(suggestions: List<String>, selected: String, onClick: (String) -> Unit) {
    val c = BarcaddyTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        suggestions.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { s ->
                    val on = selected == s
                    Box(
                        Modifier
                            .background(
                                if (on) c.ink else c.surface,
                                RoundedCornerShape(999.dp),
                            )
                            .border(
                                if (on) 0.dp else 1.5.dp,
                                c.divider,
                                RoundedCornerShape(999.dp),
                            )
                            .clickable { onClick(s) }
                            .padding(horizontal = 13.dp, vertical = 7.dp),
                    ) {
                        Text(
                            s,
                            color = if (on) c.bg else c.ink,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FlowGrid(items: List<String>, columns: Int, content: @Composable (String) -> Unit) {
    Column {
        items.chunked(columns).forEach { row ->
            Row(
                Modifier.fillMaxWidth().padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                row.forEach { content(it) }
            }
        }
    }
}
