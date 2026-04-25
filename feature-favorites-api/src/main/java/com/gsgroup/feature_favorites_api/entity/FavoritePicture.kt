package com.gsgroup.feature_favorites_api.entity

data class FavoritePicture(
    val thumbUrl: String,
    val previewUrl: String,
    val fullPicUrl: String,
    val smallUrl: String,
    val topic: String
)