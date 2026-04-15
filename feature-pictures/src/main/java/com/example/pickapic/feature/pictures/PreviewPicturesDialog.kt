package com.example.pickapic.feature.pictures

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import com.example.pickapic.uikit.components.AnimatedBoxIcon
import com.example.pickapic.uikit.components.ButtonGradientSecondary
import com.example.pickapic.uikit.theme.Shapes

@Composable
internal fun PicturePreviewDialog(
    imageUrl: String,
    onButtonClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        var isPictureShown by remember { mutableStateOf(false) }
        Surface(
            modifier = Modifier.wrapContentSize(),
            shape = Shapes.large,
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier.wrapContentSize()
            ) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    loading = { AnimatedBoxIcon(boxColor = Color.White) },
                    onSuccess = { isPictureShown = true }
                )
                ButtonGradientSecondary(
                    isVisible = isPictureShown,
                    text = stringResource(R.string.set_this_wallpaper),
                    modifier = Modifier.align(Alignment.BottomEnd),
                    onClick = onButtonClick
                )
            }
        }
    }
}
