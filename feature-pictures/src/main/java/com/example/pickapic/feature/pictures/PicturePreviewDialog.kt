package com.example.pickapic.feature.pictures

import android.content.Intent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import com.example.pickapic.uikit.components.AnimatedBoxIcon
import com.example.pickapic.uikit.components.ButtonGradientSecondary
import com.example.pickapic.uikit.components.CenteredHeartIcon
import com.example.pickapic.uikit.theme.Shapes

@Composable
internal fun PicturePreviewDialog(
    previewState: PreviewState,
    onButtonClick: () -> Unit,
    onDoubleTap: () -> Unit,
    onDismiss: () -> Unit,
) {
    val enabled = !previewState.isWallpaperSet
    val alpha by animateFloatAsState(targetValue = if (previewState.settingWallpaper) 0f else 1f)
    val buttonText = if (enabled) {
        stringResource(R.string.set_this_wallpaper)
    } else {
        stringResource(R.string.wallpaper_set)
    }
    var showHeart by remember { mutableStateOf(false) }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        var isPictureShown by remember { mutableStateOf(false) }
        val scale by rememberInfiniteTransition().animateFloat(
            initialValue = 1f,
            targetValue = 1.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        val context = LocalContext.current
        Surface(
            modifier = Modifier.wrapContentSize(),
            shape = Shapes.large,
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier.wrapContentSize()
            ) {
                SubcomposeAsyncImage(
                    model = previewState.previewUrl,
                    contentDescription = null,
                    loading = { AnimatedBoxIcon(boxColor = Color.White) },
                    onSuccess = { isPictureShown = true },
                    modifier = Modifier
                        .graphicsLayer(
                            scaleX = if (previewState.settingWallpaper) scale else 1f,
                            scaleY = if (previewState.settingWallpaper) scale else 1f
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    onDoubleTap()
                                    showHeart = true
                                }
                            )
                        }
                )
                CenteredHeartIcon(
                    isVisible = showHeart,
                    onAnimationFinished = { showHeart = false }
                )
                if (!previewState.isWallpaperSet) {
                    ButtonGradientSecondary(
                        isVisibleAnimated = isPictureShown && !previewState.settingWallpaper,
                        text = buttonText,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .graphicsLayer(alpha = alpha),
                        onClick = onButtonClick
                    )
                } else {
                    onDismiss()
                    val intent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_HOME)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                }
                if (previewState.settingWallpaper) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}
