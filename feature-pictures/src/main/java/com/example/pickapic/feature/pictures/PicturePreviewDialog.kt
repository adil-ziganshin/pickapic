package com.example.pickapic.feature.pictures

import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import com.example.pickapic.uikit.components.AnimatedBoxIcon
import com.example.pickapic.uikit.components.ButtonGradientSecondary
import com.example.pickapic.uikit.theme.Shapes

@Composable
internal fun PicturePreviewDialog(
    previewState: PreviewState,
    onButtonClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    val enabled = !previewState.isWallpaperSet
    val alpha by animateFloatAsState(targetValue = if (previewState.settingWallpaper) 0f else 1f)
    val buttonText = if (enabled) {
        stringResource(R.string.set_this_wallpaper)
    } else {
        stringResource(R.string.wallpaper_set)
    }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        var isPictureShown by remember { mutableStateOf(false) }
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
                    onSuccess = { isPictureShown = true }
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
