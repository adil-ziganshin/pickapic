package com.example.pickapic.feature.home.data

import com.example.pickapic.feature.home.domain.Topic
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class GetTopicsUseCaseImplTest {

    @Test
    fun `returns data from repository for page 1`() = runTest {
        val expected = listOf(Topic("1", "a", null))
        val repository = mockk<TopicsRepository>()
        coEvery { repository.getTopics(1, 10) } returns expected
        val useCase = GetTopicsUseCaseImpl(repository)
        assertEquals(expected, useCase(page = 1, perPage = 10))
        coVerify(exactly = 1) { repository.getTopics(1, 10) }
    }

    @Test
    fun `forwards custom page and perPage`() = runTest {
        val list = (1..3).map { Topic("$it", "t$it", "u$it") }
        val repository = mockk<TopicsRepository>()
        coEvery { repository.getTopics(3, 25) } returns list
        val useCase = GetTopicsUseCaseImpl(repository)
        assertEquals(list, useCase(3, 25))
        coVerify { repository.getTopics(3, 25) }
    }

    @Test
    fun `empty list is passed through`() = runTest {
        val repository = mockk<TopicsRepository>()
        coEvery { repository.getTopics(1, 10) } returns emptyList()
        val useCase = GetTopicsUseCaseImpl(repository)
        assertTrue(useCase(1, 10).isEmpty())
    }

    @Test
    fun `propagates repository error`() = runTest {
        val repository = mockk<TopicsRepository>()
        val error = IOException("network")
        coEvery { repository.getTopics(1, 10) } throws error
        val useCase = GetTopicsUseCaseImpl(repository)
        var caught: Throwable? = null
        try {
            useCase(1, 10)
        } catch (e: IOException) {
            caught = e
        }
        assertEquals(error, caught)
    }

    @Test
    fun `multiple invocations call repository each time`() = runTest {
        val topic = Topic("x", "y", null)
        val repository = mockk<TopicsRepository>()
        coEvery { repository.getTopics(1, 10) } returns listOf(topic)
        coEvery { repository.getTopics(2, 10) } returns listOf(topic)
        val useCase = GetTopicsUseCaseImpl(repository)
        useCase(1, 10)
        useCase(2, 10)
        coVerify(exactly = 1) { repository.getTopics(1, 10) }
        coVerify(exactly = 1) { repository.getTopics(2, 10) }
    }
}
