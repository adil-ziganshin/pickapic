package com.example.pickapic.feature.pictures.domain

data class PicturesPage(
    val totalPages: Int,
    val results: List<Picture>,
)
