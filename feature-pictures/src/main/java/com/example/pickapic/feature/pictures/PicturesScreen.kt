package com.example.pickapic.feature.pictures

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pickapic.uikit.pictures.PicturesScreen
import com.example.pickapic.uikit.pictures.PicturesScreenState

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
        onPictureDoubleTap = viewModel::onPreviewPictureDoubleTap,
        onErrorDismiss = viewModel::onErrorDismiss,
        onSetWallpaper = viewModel::onSetWallpaper
    )
}
