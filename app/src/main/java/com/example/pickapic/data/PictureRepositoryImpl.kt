package com.example.pickapic.data

import android.util.Log
import com.example.pickapic.domain.PictureRepository
import javax.inject.Inject

class PictureRepositoryImpl @Inject constructor (
    private val pictureService: PictureService
): PictureRepository<PictureApiResponse>{

    override suspend fun getData(query: String): PictureApiResponse {
        return pictureService.getData(query = query)
    }
}
