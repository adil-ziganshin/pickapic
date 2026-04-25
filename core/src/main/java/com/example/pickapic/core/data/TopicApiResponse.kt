package com.example.pickapic.core.data

import com.google.gson.annotations.SerializedName

data class TopicDto(
    val id: String,
    val slug: String,
    val title: String,
    val description: String?,
    @SerializedName("total_photos")
    val totalPhotos: Int = 0,
    @SerializedName("cover_photo")
    val coverPhoto: CoverPhotoDto?,
)

data class CoverPhotoDto(
    val id: String?,
    val urls: Urls?,
)
