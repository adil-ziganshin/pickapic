package com.example.pickapic.feature.home.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopicDto(
    val id: String,
    val slug: String,
    val title: String,
    val description: String?,
    @SerialName("total_photos")
    val totalPhotos: Int = 0,
    @SerialName("cover_photo")
    val coverPhoto: CoverPhotoDto?,
)

@Serializable
data class CoverPhotoDto(
    val id: String,
    val urls: Urls,
)

@Serializable
data class Urls(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String,
)