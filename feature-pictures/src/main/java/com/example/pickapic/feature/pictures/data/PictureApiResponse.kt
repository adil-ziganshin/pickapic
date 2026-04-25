package com.example.pickapic.feature.pictures.data

import com.google.gson.annotations.SerializedName

data class PictureApiResponse(
    val total: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
    val results: List<Result>
)

data class Result(
    val urls: Urls
)

data class Urls(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String,
)
