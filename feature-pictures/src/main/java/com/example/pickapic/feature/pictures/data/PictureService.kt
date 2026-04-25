package com.example.pickapic.feature.pictures.data

import com.example.pickapic.core.util.Constants.Companion.API_KEY
import retrofit2.http.GET
import retrofit2.http.Query

interface PictureService {
    @GET("search/photos")
    suspend fun getData(
        @Query("client_id") clientId: String = API_KEY,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int
    ): PictureApiResponse
}
