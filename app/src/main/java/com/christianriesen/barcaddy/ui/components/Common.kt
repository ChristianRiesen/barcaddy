package com.christianriesen.barcaddy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.christianriesen.barcaddy.ui.theme.BarcaddyTheme

@Composable
fun IconBtn(
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String? = null,
    tint: Color = BarcaddyTheme.colors.ink,
    size: androidx.compose.ui.unit.Dp = 44.dp,
) {
    Box(
        Modifier
            .size(size)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = contentDescription, tint = tint)
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text.uppercase(),
        color = BarcaddyTheme.colors.muted,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(start = 4.dp, top = 20.dp, bottom = 8.dp),
    )
}

@Composable
fun SettingsCard(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    danger: Boolean = false,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val c = BarcaddyTheme.colors
    val rowMod = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp)
        .background(c.surface, RoundedCornerShape(16.dp))
        .let { if (onClick != null) it.clickable(onClick = onClick) else it }
        .padding(14.dp)
    Row(rowMod, verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(36.dp)
                .background(if (danger) c.dangerSoft else c.surfaceSoft, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = if (danger) c.danger else c.ink, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                title,
                color = if (danger) c.danger else c.ink,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
            )
            if (subtitle != null) {
                Text(subtitle, color = c.muted, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
        if (trailing != null) {
            Spacer(Modifier.width(8.dp))
            trailing()
        }
    }
}

@Composable
fun BarcaddyToggle(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val c = BarcaddyTheme.colors
    Box(
        Modifier
            .size(width = 48.dp, height = 28.dp)
            .background(if (checked) c.ink else c.dividerStrong, RoundedCornerShape(14.dp))
            .clickable { onCheckedChange(!checked) },
    ) {
        Box(
            Modifier
                .padding(start = if (checked) 23.dp else 3.dp, top = 3.dp)
                .size(22.dp)
                .background(c.surface, CircleShape),
        )
    }
}

@Composable
fun PaletteSwatch(
    paletteName: String,
    monogram: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val palette = com.christianriesen.barcaddy.ui.theme.Palettes.byName(paletteName)
    Box(
        Modifier
            .size(56.dp)
            .background(palette.bg, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(monogram, color = palette.deep, fontWeight = FontWeight.SemiBold, fontSize = 22.sp)
        if (selected) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 0.dp)
                    .size(20.dp)
                    .background(BarcaddyTheme.colors.ink, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Check, null, tint = BarcaddyTheme.colors.bg, modifier = Modifier.size(12.dp))
            }
        }
    }
}

@Composable
fun ThinDivider(
    horizontalPadding: androidx.compose.ui.unit.Dp = 0.dp,
    color: Color = BarcaddyTheme.colors.divider,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
            .height(1.dp)
            .background(color),
    )
}
