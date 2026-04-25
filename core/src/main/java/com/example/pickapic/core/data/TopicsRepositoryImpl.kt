package com.example.pickapic.core.data

import com.example.pickapic.core.domain.Topic
import com.example.pickapic.core.domain.TopicsRepository
import javax.inject.Inject

class TopicsRepositoryImpl @Inject constructor(
    private val topicService: TopicService,
) : TopicsRepository {

    override suspend fun getTopics(page: Int, perPage: Int): List<Topic> {
        return topicService.getTopics(page = page, perPage = perPage)
            .map { dto ->
                Topic(
                    id = dto.id,
                    slug = dto.slug,
                    title = dto.title,
                    coverSmallUrl = dto.coverPhoto?.urls?.small,
                )
            }
    }
}
