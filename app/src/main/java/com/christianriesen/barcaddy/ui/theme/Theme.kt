package com.christianriesen.barcaddy.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class BarcaddyColors(
    val bg: Color,
    val surface: Color,
    val surfaceSoft: Color,
    val ink: Color,
    val muted: Color,
    val mutedSoft: Color,
    val divider: Color,
    val dividerStrong: Color,
    val accent: Color,
    val accentSoft: Color,
    val accentDeep: Color,
    val danger: Color,
    val dangerSoft: Color,
)

private val LightColors = BarcaddyColors(
    bg = Color(0xFFEFF1F6),
    surface = Color(0xFFFFFFFF),
    surfaceSoft = Color(0xFFE6EAF1),
    ink = Color(0xFF15171F),
    muted = Color(0xFF6B7280),
    mutedSoft = Color(0xFFA0A6B0),
    divider = Color(0xFFE1E5EC),
    dividerStrong = Color(0xFFD0D5DD),
    accent = Color(0xFFFF5C3C),
    accentSoft = Color(0xFFFFE3DC),
    accentDeep = Color(0xFF7A2418),
    danger = Color(0xFFC4392F),
    dangerSoft = Color(0xFFFBE0DC),
)

private val DarkColors = BarcaddyColors(
    bg = Color(0xFF0E1015),
    surface = Color(0xFF1A1D24),
    surfaceSoft = Color(0xFF242832),
    ink = Color(0xFFECEEF3),
    muted = Color(0xFF9CA3AF),
    mutedSoft = Color(0xFF707682),
    divider = Color(0xFF2A2F39),
    dividerStrong = Color(0xFF383D48),
    accent = Color(0xFFFF7A60),
    accentSoft = Color(0xFF3A1F18),
    accentDeep = Color(0xFFFFC4B5),
    danger = Color(0xFFF47A6E),
    dangerSoft = Color(0xFF2A1A18),
)

val LocalBarcaddyColors = staticCompositionLocalOf { LightColors }

object BarcaddyTheme {
    val colors: BarcaddyColors
        @Composable get() = LocalBarcaddyColors.current
}

private val sans = FontFamily.SansSerif

private val BarcaddyTypography = Typography(
    displayLarge   = TextStyle(fontFamily = sans, fontWeight = FontWeight.SemiBold, fontSize = 32.sp),
    headlineLarge  = TextStyle(fontFamily = sans, fontWeight = FontWeight.SemiBold, fontSize = 26.sp, letterSpacing = (-0.2).sp),
    headlineMedium = TextStyle(fontFamily = sans, fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
    titleLarge     = TextStyle(fontFamily = sans, fontWeight = FontWeight.Medium,   fontSize = 17.sp),
    titleMedium    = TextStyle(fontFamily = sans, fontWeight = FontWeight.Medium,   fontSize = 16.sp),
    bodyLarge      = TextStyle(fontFamily = sans, fontWeight = FontWeight.Normal,   fontSize = 15.sp),
    bodyMedium     = TextStyle(fontFamily = sans, fontWeight = FontWeight.Normal,   fontSize = 14.sp),
    bodySmall      = TextStyle(fontFamily = sans, fontWeight = FontWeight.Normal,   fontSize = 12.sp),
    labelLarge     = TextStyle(fontFamily = sans, fontWeight = FontWeight.Medium,   fontSize = 14.sp),
    labelSmall     = TextStyle(fontFamily = sans, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, letterSpacing = 1.0.sp),
)

@Composable
fun BarcaddyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val palette = if (darkTheme) DarkColors else LightColors
    val m3 = if (darkTheme) {
        darkColorScheme(
            primary = palette.accent,
            onPrimary = Color.White,
            background = palette.bg,
            onBackground = palette.ink,
            surface = palette.surface,
            onSurface = palette.ink,
            error = palette.danger,
        )
    } else {
        lightColorScheme(
            primary = palette.accent,
            onPrimary = Color.White,
            background = palette.bg,
            onBackground = palette.ink,
            surface = palette.surface,
            onSurface = palette.ink,
            error = palette.danger,
        )
    }
    CompositionLocalProvider(LocalBarcaddyColors provides palette) {
        MaterialTheme(
            colorScheme = m3,
            typography = BarcaddyTypography,
            content = content,
        )
    }
}
