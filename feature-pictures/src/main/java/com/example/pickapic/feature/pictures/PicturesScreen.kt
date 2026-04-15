package com.example.pickapic.feature.pictures

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pickapic.uikit.components.TitleCard
import com.example.pickapic.uikit.theme.Pencil700
import com.example.pickapic.uikit.theme.PickapicTheme

@Composable
fun PicturesScreenRoute(
    viewModel: PicturesViewModel = hiltViewModel()
) {
    val uiState: PicturesScreenState by viewModel.uiState.collectAsState()
    PicturesScreen(
        state = uiState,
        onPictureLongClick = {},
        onPreviewDismiss = viewModel::onDismissPreview,
        onPictureClick = viewModel::onPicturePreview,
        onErrorDismiss = viewModel::onErrorDismiss
    )
}

@Composable
private fun PicturesScreen(
    state: PicturesScreenState,
    onPictureClick: (String) -> Unit,
    onPictureLongClick: (String) -> Unit,
    onPreviewDismiss: () -> Unit,
    onErrorDismiss: () -> Unit
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
                    is PicturesScreenState.Empty -> Text(
                        text = stringResource(id = R.string.no_data_available),
                        modifier = Modifier.padding(16.dp),
                    )

                    is PicturesScreenState.Loading -> LoadingPlaceholder()

                    is PicturesScreenState.Error ->
                        ErrorDialog(
                            message = state.message,
                            onDismiss = onErrorDismiss
                        )

                    is PicturesScreenState.Loaded ->
                        PicturesLoadedScreen(
                            pictures = state.data,
                            previewUrl = state.previewUrl,
                            onPictureLongClick = onPictureLongClick,
                            onPreviewDismiss = onPreviewDismiss,
                            onPictureClick = onPictureClick
                        )
                }
            }
        }
    }
}

@Composable
private fun LoadingPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(alignment = Alignment.Center)
        )
    }
}

@Composable
private fun PicturesLoadedScreen(
    pictures: PicturesUiModel,
    previewUrl: String?,
    onPictureClick: (String) -> Unit,
    onPictureLongClick: (String) -> Unit,
    onPreviewDismiss: () -> Unit
) {
    val staggeredGridState = rememberLazyStaggeredGridState()

    if (previewUrl != null) {
        PicturePreviewDialog(
            imageUrl = previewUrl,
            onButtonClick = {
                onPictureClick(previewUrl)
            },
            onDismiss = onPreviewDismiss
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
                    onClick = {
                        onPictureClick(pictureUrl.full)
                    },
                    onLongClick = {
                        onPictureLongClick(pictureUrl.full)
                    }
                )
            }
        },
    )
}

@Composable
private fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.problem_occurred)) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(android.R.string.ok))
            }
        }
    )
}
