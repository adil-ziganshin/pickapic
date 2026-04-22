package com.example.pickapic.core.data

import com.example.pickapic.core.domain.PictureRepository
import javax.inject.Inject

class PictureRepositoryImpl @Inject constructor(
    private val pictureService: PictureService
) : PictureRepository<PictureApiResponse> {

    override suspend fun getData(
        query: String,
        page: Int,
        perPage: Int
    ): PictureApiResponse {
        return pictureService.getData(
            query = query,
            page = page,
            perPage = perPage
        )
    }
}
