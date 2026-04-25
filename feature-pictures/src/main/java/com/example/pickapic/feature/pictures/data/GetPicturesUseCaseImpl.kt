package com.example.pickapic.feature.pictures.data

import com.example.pickapic.feature.pictures.domain.GetPicturesUseCase
import com.example.pickapic.feature.pictures.domain.PicturesPage
import javax.inject.Inject

class GetPicturesUseCaseImpl @Inject constructor(
    private val pictureRepository: PictureRepository,
) : GetPicturesUseCase {

    override suspend fun invoke(query: String, page: Int, perPage: Int): PicturesPage {
        return pictureRepository.getData(
            query = query,
            page = page,
            perPage = perPage,
        )
    }
}