package com.example.pickapic.domain

interface PictureRepository<T> {
    suspend fun getData(query: String): T
}
