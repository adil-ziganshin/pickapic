package com.example.pickapic.feature.favorites.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pickapic.uikit.pictures.PicturesGrid
import com.example.pickapic.uikit.pictures.PicturesGridState

@Composable
fun FavoritePicScreenRoute(
    viewModel: FavoritePicturesViewModel = hiltViewModel()
) {
    val uiState: PicturesGridState by viewModel.uiState.collectAsState()
    PicturesGrid(
        state = uiState,
        onPictureClick = { previewState ->
            viewModel.onPicturePreview(previewState)
        },
        onPictureLongClick = viewModel::onPictureLongClick,
        onPictureDoubleTap = viewModel::onPreviewPictureDoubleTap,
        onPreviewDismiss = viewModel::onDismissPreview,
        onSetWallpaper = viewModel::onSetWallpaper,
        onErrorDismiss = viewModel::onErrorDismiss
    )
}
