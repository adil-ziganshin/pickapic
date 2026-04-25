package com.example.pickapic.uikit.pictures

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.pickapic.uikit.R
import com.example.pickapic.uikit.components.TitleCard
import com.example.pickapic.uikit.components.rememberHeavyFlingBehavior
import com.example.pickapic.uikit.theme.Pencil700
import com.example.pickapic.uikit.theme.PickapicTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun PicturesGrid(
    state: PicturesGridState,
    onPictureClick: (PreviewState) -> Unit,
    onPictureLongClick: (PictureUiItem) -> Unit,
    onPictureDoubleTap: (PreviewState) -> Unit,
    onPreviewDismiss: () -> Unit,
    onSetWallpaper: (String) -> Unit,
    onErrorDismiss: () -> Unit,
    onLoadMore: () -> Unit = {},
) {
    PickapicTheme {
        Scaffold(
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = contentColorFor(SnackbarDefaults.backgroundColor)
                .takeOrElse { LocalContentColor.current },
        ) {
            Column {
                TitleCard(
                    text = state.title,
                    color = Pencil700,
                )
                when (state) {
                    is PicturesGridState.Empty -> Text(
                        text = stringResource(id = R.string.no_data_available),
                        modifier = Modifier.padding(16.dp),
                    )

                    is PicturesGridState.Loading -> LoadingPlaceholder()

                    is PicturesGridState.Error ->
                        ErrorDialog(
                            message = state.message,
                            onDismiss = onErrorDismiss
                        )

                    is PicturesGridState.Loaded ->
                        PicturesLoaded(
                            pictures = state.data,
                            previewState = state.preview,
                            onPictureLongClick = onPictureLongClick,
                            onPreviewDismiss = onPreviewDismiss,
                            onPictureClick = onPictureClick,
                            onSetWallpaper = onSetWallpaper,
                            onPictureDoubleTap = onPictureDoubleTap,
                            onLoadMore = onLoadMore,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PicturesLoaded(
    pictures: PicturesUiModel,
    previewState: PreviewState?,
    onPictureClick: (PreviewState) -> Unit,
    onPictureLongClick: (PictureUiItem) -> Unit,
    onPictureDoubleTap: (PreviewState) -> Unit,
    onPreviewDismiss: () -> Unit,
    onSetWallpaper: (String) -> Unit,
    onLoadMore: () -> Unit,
) {
    val staggeredGridState = rememberLazyStaggeredGridState()

    if (previewState != null) {
        PicturePreviewDialog(
            previewState = previewState,
            onButtonClick = {
                onSetWallpaper(previewState.fullPictureUrl)
            },
            onDoubleTap = {
                onPictureDoubleTap(previewState)
            },
            onDismiss = onPreviewDismiss
        )
    }

    InfiniteListHandler(
        listState = staggeredGridState,
        totalItems = pictures.pictures.size,
        isLoadingMore = pictures.isLoadingMore,
        endReached = pictures.endReached,
        onLoadMore = onLoadMore,
    )

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(3),
        state = staggeredGridState,
        contentPadding = PaddingValues(2.dp),
        flingBehavior = rememberHeavyFlingBehavior(velocityMultiplier = 0.1f),
        content = {
            items(
                items = pictures.pictures,
                key = { it.regularUrl },
            ) { item ->
                PictureItem(
                    pictureUrl = item.thumbUrl,
                    onClick = {
                        onPictureClick(
                            PreviewState(
                                previewUrl = item.regularUrl,
                                fullPictureUrl = item.fullUrl,
                                smallUrl = item.smallUrl,
                                thumbUrl = item.thumbUrl
                            )
                        )
                    },
                    onLongClick = {
                        onPictureLongClick(item)
                    }
                )
            }
            if (pictures.isLoadingMore) {
                item(
                    key = "loading_more_footer",
                    span = StaggeredGridItemSpan.FullLine
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun InfiniteListHandler(
    listState: LazyStaggeredGridState,
    totalItems: Int,
    isLoadingMore: Boolean,
    endReached: Boolean,
    onLoadMore: () -> Unit,
    prefetchDistance: Int = 12,
) {
    val currentOnLoadMore by rememberUpdatedState(onLoadMore)
    val shouldLoadMore = remember(listState, totalItems) {
        snapshotFlow {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            totalItems > 0 && lastVisible >= totalItems - 1 - prefetchDistance
        }.distinctUntilChanged().filter { it }
    }

    LaunchedEffect(shouldLoadMore, isLoadingMore, endReached) {
        if (isLoadingMore || endReached) return@LaunchedEffect
        shouldLoadMore.collect { reached ->
            if (reached) currentOnLoadMore()
        }
    }
}

@Composable
private fun ErrorDialog(
    message: String?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.problem_occurred)) },
        text = { Text(message ?: "Unexpected error") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(android.R.string.ok))
            }
        }
    )
}
