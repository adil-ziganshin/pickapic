package com.example.pickapic.uikit.pictures

data class PictureUiItem(
    val smallUrl: String,
    val regularUrl: String,
    val fullUrl: String
)

data class PicturesUiModel(
    val pictures: List<PictureUiItem>
)

data class PreviewState(
    val smallUrl: String,
    val previewUrl: String,
    val fullPictureUrl: String,
    val settingWallpaper: Boolean = false,
    val isWallpaperSet: Boolean = false
)

sealed interface PicturesScreenState {

    val title: String

    data class Empty(override val title: String) : PicturesScreenState

    data class Loading(override val title: String) : PicturesScreenState

    data class Loaded(
        override val title: String,
        val data: PicturesUiModel,
        val preview: PreviewState? = null
    ) : PicturesScreenState

    data class Error(
        override val title: String,
        val message: String?
    ) : PicturesScreenState
}
