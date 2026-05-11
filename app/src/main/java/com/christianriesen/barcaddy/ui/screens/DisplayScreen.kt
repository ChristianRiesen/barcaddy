package com.christianriesen.barcaddy.ui.screens

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.christianriesen.barcaddy.data.Card
import com.christianriesen.barcaddy.data.CodeKind
import com.christianriesen.barcaddy.ui.components.BarcaddyFormat
import com.christianriesen.barcaddy.ui.components.CodeImage
import com.christianriesen.barcaddy.ui.theme.Palettes

@Composable
fun DisplayScreen(
    card: Card,
    showCodeValue: Boolean,
    keepAwake: Boolean,
    boostBrightness: Boolean,
    onBack: () -> Unit,
) {
    val activity = LocalContext.current as? Activity
    val window = activity?.window
    val originalBrightness = remember(card.id) { window?.attributes?.screenBrightness }
    val restoreWindow = {
        if (window != null) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            if (boostBrightness) {
                val attrs = window.attributes
                attrs.screenBrightness = originalBrightness ?: WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                window.attributes = attrs
            }
        }
    }
    DisposableEffect(card.id, keepAwake, boostBrightness) {
        if (window != null) {
            if (keepAwake) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            if (boostBrightness) {
                val attrs = window.attributes
                attrs.screenBrightness = 1f
                window.attributes = attrs
            }
        }
        onDispose { restoreWindow() }
    }
    val dismiss = {
        restoreWindow()
        onBack()
    }

    val palette = Palettes.byName(card.palette)
    val format = BarcaddyFormat.forName(card.format)
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable(onClick = dismiss),
    ) {
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 36.dp, vertical = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (card.kind == CodeKind.QR) {
                Box(
                    Modifier
                        .widthIn(max = 380.dp)
                        .fillMaxWidth()
                        .aspectRatio(1f),
                ) {
                    CodeImage(
                        value = card.value,
                        format = format,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds,
                    )
                }
            } else {
                CodeImage(
                    value = card.value,
                    format = format,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds,
                )
            }
        }

        if (showCodeValue) {
            Text(
                card.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                color = Color(0xFF6B7280),
                fontSize = 14.sp,
                letterSpacing = 2.5.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .background(palette.bg)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(42.dp)
                    .background(palette.deep, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    card.monogram,
                    color = palette.bg,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    card.name,
                    color = palette.deep,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (card.description.isNotBlank()) {
                    Text(
                        card.description,
                        color = palette.deep.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
