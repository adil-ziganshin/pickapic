package com.example.pickapic.uikit.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val ThemeColors = lightColors(
    primary = Pencil500,
    primaryVariant = Pencil400,
    onPrimary = Color.White,
    secondary = Pink500,
    secondaryVariant = Pink200,
    onSecondary = Color.White
)

@Composable
fun PickapicTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = ThemeColors,
        typography = RubikTypography,
        shapes = Shapes,
        content = content
    )
}

val gradientPrimary = Brush.horizontalGradient(
    colors = listOf(ThemeColors.primary, ThemeColors.primaryVariant)
)

val gradientSecondary = Brush.horizontalGradient(
    colors = listOf(ThemeColors.secondary, ThemeColors.secondaryVariant)
)