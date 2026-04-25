package com.example.pickapic.feature.home.domain

import com.example.pickapic.feature.home.data.TopicsRepository
import javax.inject.Inject

class GetTopicsUseCaseImpl @Inject constructor(
    private val topicsRepository: TopicsRepository,
): GetTopicsUseCase {

    override suspend operator fun invoke(page: Int, perPage: Int): List<Topic> =
        topicsRepository.getTopics(page = page, perPage = perPage)

}
