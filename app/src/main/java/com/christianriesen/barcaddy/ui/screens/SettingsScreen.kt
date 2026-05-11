package com.christianriesen.barcaddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.christianriesen.barcaddy.data.Settings
import com.christianriesen.barcaddy.ui.components.BarcaddyToggle
import com.christianriesen.barcaddy.ui.components.IconBtn
import com.christianriesen.barcaddy.ui.components.SectionLabel
import com.christianriesen.barcaddy.ui.components.SettingsCard
import com.christianriesen.barcaddy.ui.theme.BarcaddyTheme

@Composable
fun SettingsScreen(
    settings: Settings,
    onToggleDarkMode: (Boolean) -> Unit,
    onToggleKeepAwake: (Boolean) -> Unit,
    onToggleBoostBrightness: (Boolean) -> Unit,
    onToggleShowCodeValue: (Boolean) -> Unit,
    onExport: () -> Unit,
    onImport: () -> Unit,
    onClearAll: () -> Unit,
    onBack: () -> Unit,
) {
    val c = BarcaddyTheme.colors
    val scroll = rememberScrollState()
    Column(
        Modifier
            .fillMaxSize()
            .background(c.bg),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconBtn(Icons.AutoMirrored.Filled.ArrowBack, onBack, "Back")
            Spacer(Modifier.size(4.dp))
            Text("Settings", color = c.ink, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }

        Column(
            Modifier
                .verticalScroll(scroll)
                .padding(horizontal = 20.dp, vertical = 4.dp),
        ) {
            SectionLabel("Appearance")
            SettingsCard(
                icon = Icons.Default.DarkMode,
                title = "Dark mode",
                subtitle = "Use a dark theme throughout the app",
                trailing = { BarcaddyToggle(settings.darkMode, onToggleDarkMode) },
            )

            SectionLabel("Display")
            SettingsCard(
                icon = Icons.Default.Brightness5,
                title = "Boost brightness when showing",
                subtitle = "Maxes out the screen so scanners read codes faster",
                trailing = { BarcaddyToggle(settings.boostBrightness, onToggleBoostBrightness) },
            )
            SettingsCard(
                icon = Icons.Default.RemoveRedEye,
                title = "Keep screen awake while displaying",
                subtitle = "Prevents the screen from dimming or locking",
                trailing = { BarcaddyToggle(settings.keepAwake, onToggleKeepAwake) },
            )
            SettingsCard(
                icon = Icons.Default.TextFields,
                title = "Show code value below",
                subtitle = "Display the raw value under the code",
                trailing = { BarcaddyToggle(settings.showCodeValue, onToggleShowCodeValue) },
            )

            SectionLabel("Data")
            SettingsCard(
                icon = Icons.Default.Upload,
                title = "Export as CSV",
                subtitle = "Save a CSV of all cards",
                onClick = onExport,
            )
            SettingsCard(
                icon = Icons.Default.Download,
                title = "Import from CSV",
                subtitle = "Load cards from a CSV file",
                onClick = onImport,
            )
            SettingsCard(
                icon = Icons.Default.Delete,
                title = "Clear all cards",
                subtitle = "Permanently delete every card on this device",
                danger = true,
                onClick = onClearAll,
            )

            SectionLabel("About")
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = 4.dp),
            ) {
                Text(
                    "Barcaddy 0.1.0\nMade with care. Codes never leave your device.",
                    color = c.muted,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                )
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}
