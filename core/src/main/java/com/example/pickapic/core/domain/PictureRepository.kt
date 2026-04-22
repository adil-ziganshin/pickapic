package com.example.pickapic.core.domain

interface PictureRepository<T> {
    suspend fun getData(query: String, page: Int, perPage: Int): T
}
