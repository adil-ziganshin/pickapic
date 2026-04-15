package com.example.pickapic.feature.pictures

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.pickapic.uikit.components.TitleCard
import com.example.pickapic.uikit.theme.Pencil700
import com.example.pickapic.uikit.theme.PickapicTheme
import com.example.pickapic.uikit.theme.Shapes
import kotlinx.coroutines.delay

@Composable
fun PicturesScreenRoute(
    viewModel: PicturesViewModel = hiltViewModel()
) {
    val uiState: PicturesViewModel.PicturesScreenState by viewModel.uiState.collectAsState()
    PicturesScreen(state = uiState)
}

@Composable
private fun PicturesScreen(
    state: PicturesViewModel.PicturesScreenState
) {
    PickapicTheme {
        Scaffold(
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = contentColorFor(SnackbarDefaults.backgroundColor)
                .takeOrElse { LocalContentColor.current },
        ) {
            Column {
                TitleCard(
                    text = if (state.topic.length < 14) state.topic else "Search",
                    color = Pencil700,
                )
                when (state) {
                    is PicturesViewModel.PicturesScreenState.Empty -> Text(
                        text = stringResource(id = R.string.no_data_available),
                        modifier = Modifier.padding(16.dp),
                    )

                    is PicturesViewModel.PicturesScreenState.Loading -> LoadingPlaceholder()

                    is PicturesViewModel.PicturesScreenState.Error ->
                        ErrorDialog(message = state.message)

                    is PicturesViewModel.PicturesScreenState.Loaded ->
                        PicturesLoadedScreen(pictures = state.data)
                }
            }
        }
    }
}

@Composable
private fun LoadingPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun PicturesLoadedScreen(pictures: PicturesUiModel) {
    var previewUrl by remember { mutableStateOf<String?>(null) }
    val staggeredGridState = rememberLazyStaggeredGridState()

    previewUrl?.let { url ->
        PicturePreviewDialog(
            imageUrl = url,
            onDismiss = { previewUrl = null },
        )
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(3),
        state = staggeredGridState,
        contentPadding = PaddingValues(2.dp),
        content = {
            items(
                items = pictures.pictures,
                key = { it.urls.regular },
            ) { item ->
                val pictureUrl = item.urls
                PictureItem(
                    pictureUrl = pictureUrl.small,
                    onClick = {},
                    onLongClick = { previewUrl = pictureUrl.regular }
                )
            }
        },
    )
}

@Composable
private fun PicturePreviewDialog(
    imageUrl: String,
    onDismiss: () -> Unit,
) {
    var likedOverlay by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier.wrapContentSize(),
            shape = Shapes.large,
        ) {
            Column(
                modifier = Modifier.wrapContentSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(modifier = Modifier.wrapContentSize()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onDoubleTap = { likedOverlay = true },
                                )
                            },
                    )
                    if (likedOverlay) {
                        AnimatedHeartIcon(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(64.dp),
                            heartSize = 64.dp,
                            heartColor = Color.White,
                        )
                        LaunchedEffect(Unit) {
                            delay(800)
                            likedOverlay = false
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorDialog(message: String) {
    var visible by remember { mutableStateOf(true) }
    if (!visible) return

    AlertDialog(
        onDismissRequest = { visible = false },
        title = { Text(text = stringResource(R.string.problem_occurred)) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = { visible = false }) {
                Text(text = stringResource(android.R.string.ok))
            }
        },
    )
}

@Composable
private fun AnimatedHeartIcon(
    modifier: Modifier = Modifier,
    heartColor: Color = Color.Red,
    heartSize: Dp = 128.dp,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val size by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400),
            repeatMode = RepeatMode.Reverse,
        ),
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    Box(
        modifier = modifier.size(heartSize * size),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = heartColor.copy(alpha = alpha),
            modifier = Modifier.size(heartSize * size),
        )
    }
}
