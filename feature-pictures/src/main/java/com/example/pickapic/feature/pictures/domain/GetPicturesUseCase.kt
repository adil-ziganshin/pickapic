package com.example.pickapic.feature.pictures.domain

interface GetPicturesUseCase {
    suspend operator fun invoke(query: String, page: Int, perPage: Int): PicturesPage
}