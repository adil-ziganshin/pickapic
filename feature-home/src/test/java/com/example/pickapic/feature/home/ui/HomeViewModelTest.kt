package com.example.pickapic.feature.home.ui

import android.util.Log
import com.example.pickapic.feature.home.MainDispatcherRule
import com.example.pickapic.feature.home.domain.GetTopicsUseCase
import com.example.pickapic.feature.home.domain.Topic
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import retrofit2.HttpException
import retrofit2.Response
import java.util.concurrent.TimeoutException

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainRule: TestRule = MainDispatcherRule()

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    private suspend fun awaitCondition(
        timeoutMs: Long = 8000,
        block: () -> Boolean
    ) {
        val end = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < end) {
            if (block()) return
            delay(20)
        }
        if (!block()) {
            throw TimeoutException("condition not met within ${timeoutMs}ms")
        }
    }

    @Test
    fun `initial load appends topics and clears loading`() = runBlocking {
        val getTopics = mockk<GetTopicsUseCase>()
        val topics = listOf(Topic("1", "A", "https://a"))
        coEvery { getTopics(1, 10) } returns topics
        val viewModel = HomeViewModel(getTopics)
        awaitCondition { !viewModel.uiState.value.isInitialLoading }
        with(viewModel.uiState.value) {
            assertEquals(1, this.topics.size)
            assertEquals("A", this.topics[0].title)
            assertTrue(endReached)
        }
    }

    @Test
    fun `ten topics means more pages may exist`() = runBlocking {
        val getTopics = mockk<GetTopicsUseCase>()
        val list = (1..10).map { Topic("$it", "t$it", null) }
        coEvery { getTopics(1, 10) } returns list
        val viewModel = HomeViewModel(getTopics)
        awaitCondition { !viewModel.uiState.value.isInitialLoading }
        with(viewModel.uiState.value) {
            assertEquals(10, topics.size)
            assertFalse(endReached)
        }
    }

    @Test
    fun `second page appends topics`() = runBlocking {
        val getTopics = mockk<GetTopicsUseCase>()
        val page1 = (1..10).map { Topic("p$it", "a$it", null) }
        val page2 = (1..3).map { Topic("p2_$it", "b$it", null) }
        coEvery { getTopics(1, 10) } returns page1
        coEvery { getTopics(2, 10) } returns page2
        val viewModel = HomeViewModel(getTopics)
        awaitCondition { !viewModel.uiState.value.isInitialLoading }
        assertFalse(viewModel.uiState.value.endReached)
        viewModel.loadNextPage()
        awaitCondition { viewModel.uiState.value.topics.size == 13 }
        assertEquals(13, viewModel.uiState.value.topics.size)
        assertTrue(viewModel.uiState.value.endReached)
        coVerify(exactly = 1) { getTopics(1, 10) }
        coVerify(exactly = 1) { getTopics(2, 10) }
    }

    @Test
    fun `first page empty marks end`() = runBlocking {
        val getTopics = mockk<GetTopicsUseCase>()
        coEvery { getTopics(1, 10) } returns emptyList()
        val viewModel = HomeViewModel(getTopics)
        awaitCondition { !viewModel.uiState.value.isInitialLoading }
        with(viewModel.uiState.value) {
            assertTrue(topics.isEmpty())
            assertTrue(endReached)
        }
    }

    @Test
    fun `onErrorDismiss clears error`() = runBlocking {
        val getTopics = mockk<GetTopicsUseCase>()
        val http = HttpException(Response.error<Any>(500, ByteArray(0).toResponseBody(null)))
        coEvery { getTopics(1, 10) } throws http
        val viewModel = HomeViewModel(getTopics)
        awaitCondition { viewModel.uiState.value.errorMessage != null }
        assertEquals("Something went wrong", viewModel.uiState.value.errorMessage)
        viewModel.onErrorDismiss()
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onErrorDismiss when no error keeps state`() = runBlocking {
        val getTopics = mockk<GetTopicsUseCase>()
        coEvery { getTopics(1, 10) } returns listOf(Topic("1", "x", null))
        val viewModel = HomeViewModel(getTopics)
        awaitCondition { !viewModel.uiState.value.isInitialLoading }
        viewModel.onErrorDismiss()
        assertNull(viewModel.uiState.value.errorMessage)
        assertEquals(1, viewModel.uiState.value.topics.size)
    }

    @Test
    fun `rate limit shows dedicated message`() = runBlocking {
        val getTopics = mockk<GetTopicsUseCase>()
        val http = HttpException(Response.error<Any>(429, ByteArray(0).toResponseBody(null)))
        coEvery { getTopics(1, 10) } throws http
        val viewModel = HomeViewModel(getTopics)
        awaitCondition { viewModel.uiState.value.errorMessage != null }
        assertEquals("Query Limit Reached", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `generic first load error`() = runBlocking {
        val getTopics = mockk<GetTopicsUseCase>()
        coEvery { getTopics(1, 10) } throws RuntimeException("x")
        val viewModel = HomeViewModel(getTopics)
        awaitCondition { viewModel.uiState.value.errorMessage != null }
        assertEquals("Something went wrong", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `second page failure shows error`() = runBlocking {
        val getTopics = mockk<GetTopicsUseCase>()
        val p1 = (1..10).map { Topic("$it", "t$it", null) }
        coEvery { getTopics(1, 10) } returns p1
        coEvery { getTopics(2, 10) } throws HttpException(
            Response.error<Any>(400, ByteArray(0).toResponseBody(null))
        )
        val viewModel = HomeViewModel(getTopics)
        awaitCondition { !viewModel.uiState.value.isInitialLoading }
        viewModel.loadNextPage()
        awaitCondition { viewModel.uiState.value.errorMessage != null }
        assertEquals("Something went wrong", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `loadNextPage does not fetch when end reached`() = runBlocking {
        val getTopics = mockk<GetTopicsUseCase>()
        val list = (1..5).map { i -> Topic(i.toString(), "t$i", null) }
        coEvery { getTopics(1, 10) } returns list
        val viewModel = HomeViewModel(getTopics)
        awaitCondition { !viewModel.uiState.value.isInitialLoading }
        assertTrue(viewModel.uiState.value.endReached)
        coVerify(exactly = 1) { getTopics(1, 10) }
        viewModel.loadNextPage()
        delay(200)
        coVerify(exactly = 1) { getTopics(1, 10) }
        coVerify(exactly = 0) { getTopics(2, 10) }
    }

    @Test
    fun `concurrent second load is ignored while first job running`() = runBlocking {
        val getTopics = mockk<GetTopicsUseCase>()
        coEvery { getTopics(1, 10) } coAnswers { delay(300); (1..10).map { Topic("$it", "t$it", null) } }
        val viewModel = HomeViewModel(getTopics)
        delay(10)
        viewModel.loadNextPage()
        delay(10)
        viewModel.loadNextPage()
        awaitCondition { !viewModel.uiState.value.isInitialLoading }
        coVerify(exactly = 1) { getTopics(1, 10) }
    }
}
