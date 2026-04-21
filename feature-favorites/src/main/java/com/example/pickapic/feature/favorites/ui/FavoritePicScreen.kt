package com.example.pickapic.feature.favorites.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pickapic.uikit.pictures.PicturesScreen
import com.example.pickapic.uikit.pictures.PicturesScreenState

@Composable
fun FavoritePicScreen(
    viewModel: FavoritePicturesViewModel = hiltViewModel()
) {
    val uiState: PicturesScreenState by viewModel.uiState.collectAsState()
    PicturesScreen(
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
