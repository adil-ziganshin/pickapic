package com.example.pickapic.feature.home.data

import com.example.pickapic.feature.home.domain.Topic
import javax.inject.Inject

class TopicsRepositoryImpl @Inject constructor(
    private val topicService: TopicService,
) : TopicsRepository {

    override suspend fun getTopics(page: Int, perPage: Int): List<Topic> {
        return topicService.getTopics(page = page, perPage = perPage)
            .map { dto ->
                Topic(
                    id = dto.id,
                    title = dto.title,
                    coverSmallUrl = dto.coverPhoto?.urls?.small,
                )
            }
    }
}