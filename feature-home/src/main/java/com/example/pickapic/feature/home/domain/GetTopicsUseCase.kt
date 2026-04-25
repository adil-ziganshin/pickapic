package com.example.pickapic.feature.home.domain

import com.example.pickapic.core.domain.Topic
import com.example.pickapic.core.domain.TopicsRepository
import javax.inject.Inject

class GetTopicsUseCase @Inject constructor(
    private val topicsRepository: TopicsRepository,
) {
    suspend operator fun invoke(page: Int, perPage: Int = DEFAULT_PER_PAGE): List<Topic> =
        topicsRepository.getTopics(page = page, perPage = perPage)

    companion object {
        const val DEFAULT_PER_PAGE = 10
    }
}
