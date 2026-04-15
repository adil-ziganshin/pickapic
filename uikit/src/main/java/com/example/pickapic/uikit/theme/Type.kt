package com.example.pickapic.uikit.theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val RubikTypography = Typography(
    defaultFontFamily = FontFamily.SansSerif,
    h1 = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 96.sp,
        letterSpacing = (-1.5).sp
    ),
    h4 = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        letterSpacing = (-1.5).sp,
        color = Color.White
    ),
    h6 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        letterSpacing = (-1.5).sp,
        color = Color.White
    ),
    body1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = (-1.5).sp,
        color = Color.White
    ),
    body2 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = (-1.5).sp,
        color = Color.Black
    )
)
