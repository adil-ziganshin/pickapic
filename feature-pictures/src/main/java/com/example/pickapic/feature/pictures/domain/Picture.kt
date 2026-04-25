package com.example.pickapic.feature.pictures.domain

data class Picture(
    val rawUrl: String,
    val fullUrl: String,
    val regularUrl: String,
    val smallUrl: String,
    val thumbUrl: String,
)
