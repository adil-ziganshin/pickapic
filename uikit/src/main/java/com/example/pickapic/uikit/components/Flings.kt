package com.example.pickapic.uikit.components

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberHeavyFlingBehavior(
    velocityMultiplier: Float = 0.45f,
): FlingBehavior {
    val defaultFlingBehavior = ScrollableDefaults.flingBehavior()
    return remember(defaultFlingBehavior, velocityMultiplier) {
        object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                return with(defaultFlingBehavior) {
                    performFling(initialVelocity * velocityMultiplier)
                }
            }
        }
    }
}