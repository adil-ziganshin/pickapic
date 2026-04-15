package com.example.pickapic.uikit.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pickapic.uikit.theme.Shapes
import com.example.pickapic.uikit.theme.gradientSecondary
import com.example.pickapic.uikit.theme.gradientPrimary

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ButtonGradientPrimary(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    text: String,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(500)) +
                scaleIn(initialScale = 0.8f, animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(300)) +
                scaleOut(targetScale = 0.8f, animationSpec = tween(300)),
        modifier = modifier
            .padding(16.dp)
    ) {
        Button(
            onClick = onClick,
            elevation = ButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 16.dp,
                hoveredElevation = 12.dp
            ),
            contentPadding = PaddingValues(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent
            ),
            shape = Shapes.medium,

        ) {
            Box(
                modifier = Modifier
                    .background(gradientPrimary)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = text
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ButtonGradientSecondary(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    text: String,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(500)) +
                scaleIn(initialScale = 0.8f, animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(300)) +
                scaleOut(targetScale = 0.8f, animationSpec = tween(300)),
        modifier = modifier
            .padding(16.dp)
    ) {
        Button(
            onClick = onClick,
            elevation = ButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 16.dp,
                hoveredElevation = 12.dp
            ),
            contentPadding = PaddingValues(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent
            ),
            shape = Shapes.medium,

            ) {
            Box(
                modifier = Modifier
                    .background(gradientSecondary)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = text
                )
            }
        }
    }
}