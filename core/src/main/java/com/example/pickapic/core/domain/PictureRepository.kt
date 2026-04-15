package com.example.pickapic.core.domain

interface PictureRepository<T> {
    suspend fun getData(query: String): T
}
