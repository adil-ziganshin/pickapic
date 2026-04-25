package com.example.pickapic.feature.home.data

import com.example.pickapic.feature.home.domain.Topic

interface TopicsRepository {

    suspend fun getTopics(page: Int, perPage: Int): List<Topic>
}