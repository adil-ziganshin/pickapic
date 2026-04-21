package com.example.pickapic.feature.favorites.domain

data class FavoritePicture(
    val id: Long,
    val previewUrl: String,
    val fullPicUrl: String,
    val smallUrl: String,
    val topic: String
)
