package com.example.pickapic.core.data

import com.example.pickapic.core.util.Constants.Companion.API_KEY
import retrofit2.http.GET
import retrofit2.http.Query

interface TopicService {
    @GET("topics")
    suspend fun getTopics(
        @Query("client_id") clientId: String = API_KEY,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = DEFAULT_PER_PAGE,
        @Query("order_by") orderBy: String = "latest",
    ): List<TopicDto>

    companion object {
        const val DEFAULT_PER_PAGE = 10
    }
}
