package com.example.pickapic.feature.pictures

import com.example.pickapic.core.data.Result

sealed interface PicturesScreenState {

    val topic: String

    data class Empty(override val topic: String) : PicturesScreenState

    data class Loading(override val topic: String) : PicturesScreenState

    data class Loaded(
        override val topic: String,
        val data: PicturesUiModel,
        val previewUrl: String? = null
    ) : PicturesScreenState

    data class Error(
        override val topic: String,
        val message: String
    ) : PicturesScreenState
}

data class PicturesUiModel(
    val pictures: List<Result>
)
