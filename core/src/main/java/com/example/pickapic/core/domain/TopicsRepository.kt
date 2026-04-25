package com.example.pickapic.core.domain

interface TopicsRepository {
    suspend fun getTopics(page: Int, perPage: Int): List<Topic>
}
