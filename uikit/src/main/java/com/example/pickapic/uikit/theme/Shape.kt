package com.example.pickapic.uikit.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(16.dp)
)

val SemiRoundedShapes = Shapes(
    small = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp),
    medium = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
    large = RoundedCornerShape(bottomStart = 32.dp)
)
