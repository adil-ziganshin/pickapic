package com.example.pickapic.core.data

import com.example.pickapic.core.util.Constants.Companion.API_KEY
import retrofit2.http.GET
import retrofit2.http.Query

interface PictureService {
    @GET("photos")
    suspend fun getData(
        @Query("client_id") clientId: String = API_KEY,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = DEFAULT_PER_PAGE
    ): PictureApiResponse

    companion object {
        const val DEFAULT_PER_PAGE = 30
    }
}
