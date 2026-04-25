package com.example.pickapic.feature.pictures.data

import com.example.pickapic.feature.pictures.domain.Picture
import com.example.pickapic.feature.pictures.domain.PicturesPage
import javax.inject.Inject




class PictureRepositoryImpl @Inject constructor(
    private val pictureService: PictureService
) : PictureRepository {

    override suspend fun getData(
        query: String,
        page: Int,
        perPage: Int
    ): PicturesPage {
        val response = pictureService.getData(
            query = query,
            page = page,
            perPage = perPage
        )
        return response.toDomainEntity()
    }
}

private fun PictureApiResponse.toDomainEntity(): PicturesPage =
    PicturesPage(
        totalPages = totalPages,
        results = results.map { it.toDomainEntity() },
    )

private fun Result.toDomainEntity(): Picture =
    Picture(
        rawUrl = urls.raw,
        fullUrl = urls.full,
        regularUrl = urls.regular,
        smallUrl = urls.small,
        thumbUrl = urls.thumb,
    )
