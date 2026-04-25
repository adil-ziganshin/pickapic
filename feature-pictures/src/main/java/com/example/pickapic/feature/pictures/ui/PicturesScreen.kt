package com.example.pickapic.feature.pictures.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pickapic.uikit.pictures.PicturesGrid
import com.example.pickapic.uikit.pictures.PicturesGridState

@Composable
fun PicturesScreenRoute(
    viewModel: PicturesViewModel = hiltViewModel()
) {
    val uiState: PicturesGridState by viewModel.uiState.collectAsState()
    PicturesGrid(
        state = uiState,
        onPictureLongClick = {},
        onPreviewDismiss = viewModel::onDismissPreview,
        onPictureClick = viewModel::onPicturePreview,
        onPictureDoubleTap = viewModel::onPreviewPictureDoubleTap,
        onErrorDismiss = viewModel::onErrorDismiss,
        onSetWallpaper = viewModel::onSetWallpaper,
        onLoadMore = viewModel::loadNextPage,
    )
}
