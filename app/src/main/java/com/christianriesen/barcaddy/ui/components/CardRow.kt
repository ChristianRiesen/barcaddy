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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.christianriesen.barcaddy.data.Card
import com.christianriesen.barcaddy.ui.theme.BarcaddyTheme
import com.christianriesen.barcaddy.ui.theme.Palettes

@Composable
fun CardRow(
    card: Card,
    onOpen: () -> Unit,
    onMore: () -> Unit,
) {
    val palette = Palettes.byName(card.palette)
    val c = BarcaddyTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(18.dp), clip = false)
            .background(c.surface, RoundedCornerShape(18.dp))
            .clickable(onClick = onOpen),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .width(84.dp)
                .height(72.dp)
                .background(palette.bg),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                card.monogram,
                color = palette.deep,
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
                letterSpacing = (-0.5).sp,
            )
        }
        Column(
            Modifier
                .weight(1f)
                .padding(start = 18.dp, top = 14.dp, bottom = 14.dp, end = 4.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                card.name,
                color = c.ink,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (card.description.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    card.description,
                    color = c.muted,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Box(
            Modifier
                .size(40.dp)
                .padding(end = 4.dp)
                .background(c.surface, CircleShape)
                .clickable(onClick = onMore),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.MoreVert, "More", tint = c.muted, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(8.dp))
    }
}
