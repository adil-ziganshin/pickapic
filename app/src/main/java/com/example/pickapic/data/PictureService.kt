package com.example.pickapic.data

import com.example.pickapic.util.Constants.Companion.API_KEY
import retrofit2.http.GET
import retrofit2.http.Query
interface PictureService {
    @GET("photos")
    suspend fun getData(
        @Query("client_id") clientId: String = API_KEY,
        @Query("query") query: String,
        @Query("page") page: Int = 2,
        @Query("per_page") perPage: Int = 30
    ): PictureApiResponse
}
