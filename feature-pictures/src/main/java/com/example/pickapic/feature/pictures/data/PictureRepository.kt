package com.example.pickapic.feature.pictures.data

import com.example.pickapic.feature.pictures.domain.PicturesPage


interface PictureRepository {
    suspend fun getData(query: String, page: Int, perPage: Int): PicturesPage
}