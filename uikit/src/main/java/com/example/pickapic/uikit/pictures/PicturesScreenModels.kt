package com.example.pickapic.uikit.pictures

data class PictureUiItem(
    val thumbUrl: String,
    val smallUrl: String,
    val regularUrl: String,
    val fullUrl: String
)

data class PicturesUiModel(
    val pictures: List<PictureUiItem>,
    val isLoadingMore: Boolean = false,
    val endReached: Boolean = false,
)

data class PreviewState(
    val thumbUrl: String,
    val smallUrl: String,
    val previewUrl: String,
    val fullPictureUrl: String,
    val settingWallpaper: Boolean = false,
    val isWallpaperSet: Boolean = false
)

sealed interface PicturesGridState {

    val title: String

    data class Empty(override val title: String) : PicturesGridState

    data class Loading(override val title: String) : PicturesGridState

    data class Loaded(
        override val title: String,
        val data: PicturesUiModel,
        val preview: PreviewState? = null
    ) : PicturesGridState

    data class Error(
        override val title: String,
        val message: String?
    ) : PicturesGridState
}
