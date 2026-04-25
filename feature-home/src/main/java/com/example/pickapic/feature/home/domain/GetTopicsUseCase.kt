package com.example.pickapic.feature.home.domain

interface GetTopicsUseCase {

    suspend operator fun invoke(page: Int, perPage: Int): List<Topic>
}