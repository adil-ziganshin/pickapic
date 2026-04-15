package com.example.pickapic.core.data

import kotlinx.serialization.Serializable

@Serializable
data class PictureApiResponse(
    val total: Int,
    val totalPages: Int,
    val results: List<Result>
)

@Serializable
data class Result(
    val urls: Urls
)

@Serializable
data class Urls(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String,
)
