package com.lickzy.fincalc.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

object FinColors {
    val Ink = Color(0xFF201D2A)
    val InkSoft = Color(0xFF322E3B)
    val Cream = Color(0xFFFFF3DC)
    val CreamBright = Color(0xFFFFFAEF)
    val Lime = Color(0xFFC7ED4B)
    val Orange = Color(0xFFF27B4A)
    val Lavender = Color(0xFFB9AEF4)
    val Pink = Color(0xFFF1A0BD)
    val Muted = Color(0xFF746D7B)
}

private val LightColors = lightColorScheme(
    primary = FinColors.Lime,
    onPrimary = FinColors.Ink,
    primaryContainer = Color(0xFFE6F5A7),
    onPrimaryContainer = FinColors.Ink,
    secondary = FinColors.Orange,
    tertiary = FinColors.Lavender,
    background = FinColors.Cream,
    surface = FinColors.CreamBright,
    surfaceVariant = Color(0xFFEFE5D0),
    onSurface = FinColors.Ink,
    onSurfaceVariant = Color(0xFF625B69),
    outline = Color(0xFFB8AD9B),
    error = Color(0xFFB72E3B),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFD1F477),
    onPrimary = FinColors.Ink,
    primaryContainer = Color(0xFF3C4725),
    onPrimaryContainer = Color(0xFFE9F7BC),
    secondary = Color(0xFFFF9A69),
    tertiary = Color(0xFFC9BCFF),
    background = Color(0xFF191720),
    surface = Color(0xFF25222D),
    surfaceVariant = Color(0xFF34303C),
    onSurface = FinColors.CreamBright,
    onSurfaceVariant = Color(0xFFC6BECA),
    outline = Color(0xFF5A5362),
    error = Color(0xFFFFB4AB),
)

private val FinCalcTypography = Typography().copy(
    displayLarge = Typography().displayLarge.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
    displayMedium = Typography().displayMedium.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
    headlineLarge = Typography().headlineLarge.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
    headlineMedium = Typography().headlineMedium.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold),
    titleLarge = Typography().titleLarge.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
    titleMedium = Typography().titleMedium.copy(fontWeight = FontWeight.SemiBold),
    labelSmall = Typography().labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
    labelMedium = Typography().labelMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
)

private val FinCalcShapes = Shapes(
    extraSmall = RoundedCornerShape(2.dp),
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp),
    extraLarge = RoundedCornerShape(16.dp),
)

@Composable
fun FinCalcTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = FinCalcTypography,
        shapes = FinCalcShapes,
        content = content,
    )
}
