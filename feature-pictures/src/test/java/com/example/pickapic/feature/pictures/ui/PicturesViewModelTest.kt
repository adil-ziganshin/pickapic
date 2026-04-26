package com.example.pickapic.feature.pictures.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.example.pickapic.feature.pictures.domain.GetPicturesUseCase
import com.example.pickapic.feature.pictures.domain.Picture
import com.example.pickapic.feature.pictures.domain.PicturesPage
import com.example.pickapic.uikit.pictures.PicturesGridState
import com.example.pickapic.uikit.pictures.PreviewState
import com.example.pickapic.wallpaper.domain.SetWallpaperUseCase
import com.example.pickapic.feature.pictures.MainDispatcherRule
import com.gsgroup.feature_favorites_api.usecase.AddToFavoritesUseCase
import com.gsgroup.feature_favorites_api.entity.FavoritePicture
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
class PicturesViewModelTest {

    @get:Rule
    val mainRule: TestRule = MainDispatcherRule()

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    private fun picture(reg: String) = Picture(
        rawUrl = "raw_$reg",
        fullUrl = "full_$reg",
        regularUrl = reg,
        smallUrl = "s_$reg",
        thumbUrl = "t_$reg"
    )

    private suspend fun awaitCondition(timeoutMs: Long = 8000, block: () -> Boolean) {
        val end = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < end) {
            if (block()) return
            delay(20)
        }
        if (!block()) throw TimeoutException("timeout")
    }

    private fun vm(
        topic: String,
        get: GetPicturesUseCase,
        setWall: SetWallpaperUseCase = mockk<SetWallpaperUseCase>().apply {
            coEvery { setWallpaper(any()) } returns Result.success(Unit)
        },
        add: AddToFavoritesUseCase = mockk<AddToFavoritesUseCase>(relaxed = true)
    ) = PicturesViewModel(
        SavedStateHandle(mapOf("topic" to topic)),
        get,
        setWall,
        add
    )

    @Test
    fun `short topic uses topic as title`() = runBlocking {
        val get = mockk<GetPicturesUseCase>()
        val p = listOf(picture("r1"))
        coEvery { get("kittens", 1, 30) } returns PicturesPage(1, p)
        val viewModel = vm("kittens", get)
        awaitCondition { viewModel.uiState.value is PicturesGridState.Loaded }
        assertEquals("kittens", (viewModel.uiState.value as PicturesGridState.Loaded).title)
    }

    @Test
    fun `long topic uses Search as title`() = runBlocking {
        val get = mockk<GetPicturesUseCase>()
        val t = "x".repeat(20)
        coEvery { get(t, 1, 30) } returns PicturesPage(1, listOf(picture("r1")))
        val viewModel = vm(t, get)
        awaitCondition { viewModel.uiState.value is PicturesGridState.Loaded }
        assertEquals("Search", (viewModel.uiState.value as PicturesGridState.Loaded).title)
    }

    @Test
    fun `first page success shows loaded grid`() = runBlocking {
        val get = mockk<GetPicturesUseCase>()
        coEvery { get("q", 1, 30) } returns PicturesPage(2, listOf(picture("a"), picture("b")))
        val viewModel = vm("q", get)
        awaitCondition { viewModel.uiState.value is PicturesGridState.Loaded }
        val s = viewModel.uiState.value as PicturesGridState.Loaded
        assertEquals(2, s.data.pictures.size)
    }

    @Test
    fun `first page 429 shows error`() = runBlocking {
        val get = mockk<GetPicturesUseCase>()
        coEvery { get("q", 1, 30) } throws HttpException(
            Response.error<Any>(429, ByteArray(0).toResponseBody(null))
        )
        val viewModel = vm("q", get)
        awaitCondition { viewModel.uiState.value is PicturesGridState.Error }
        assertEquals(
            "Query Limit Reached",
            (viewModel.uiState.value as PicturesGridState.Error).message
        )
    }

    @Test
    fun `first page other error`() = runBlocking {
        val get = mockk<GetPicturesUseCase>()
        coEvery { get("q", 1, 30) } throws HttpException(
            Response.error<Any>(500, ByteArray(0).toResponseBody(null))
        )
        val viewModel = vm("q", get)
        awaitCondition { viewModel.uiState.value is PicturesGridState.Error }
        assertEquals(
            "Something went wrong",
            (viewModel.uiState.value as PicturesGridState.Error).message
        )
    }

    @Test
    fun `onErrorDismiss returns empty for topic title`() = runBlocking {
        val get = mockk<GetPicturesUseCase>()
        coEvery { get("cats", 1, 30) } throws HttpException(
            Response.error<Any>(500, ByteArray(0).toResponseBody(null))
        )
        val viewModel = vm("cats", get)
        awaitCondition { viewModel.uiState.value is PicturesGridState.Error }
        viewModel.onErrorDismiss()
        assertTrue(viewModel.uiState.value is PicturesGridState.Empty)
        assertEquals("cats", (viewModel.uiState.value as PicturesGridState.Empty).title)
    }

    @Test
    fun `load more appends and deduplicates by regular url`() = runBlocking {
        val get = mockk<GetPicturesUseCase>()
        val dup = picture("dup")
        coEvery { get("x", 1, 30) } returns PicturesPage(2, listOf(dup, picture("u2")))
        coEvery { get("x", 2, 30) } returns PicturesPage(2, listOf(dup, picture("u3")))
        val viewModel = vm("x", get)
        awaitCondition { viewModel.uiState.value is PicturesGridState.Loaded }
        viewModel.loadNextPage()
        awaitCondition {
            (viewModel.uiState.value as? PicturesGridState.Loaded)
                ?.data?.pictures?.size == 3
        }
        val list = (viewModel.uiState.value as PicturesGridState.Loaded).data.pictures
        assertEquals(3, list.size)
    }

    @Test
    fun `load more generic error stops spinner`() = runBlocking {
        val get = mockk<GetPicturesUseCase>()
        val many = (1..30).map { picture("p$it") }
        coEvery { get("x", 1, 30) } returns PicturesPage(2, many)
        coEvery { get("x", 2, 30) } throws HttpException(
            Response.error<Any>(400, ByteArray(0).toResponseBody(null))
        )
        val viewModel = vm("x", get)
        awaitCondition { viewModel.uiState.value is PicturesGridState.Loaded }
        viewModel.loadNextPage()
        awaitCondition {
            val st = viewModel.uiState.value as? PicturesGridState.Loaded
            st != null && !st.data.isLoadingMore
        }
        assertTrue(viewModel.uiState.value is PicturesGridState.Loaded)
    }

    @Test
    fun `load more 429 shows error`() = runBlocking {
        val get = mockk<GetPicturesUseCase>()
        val many = (1..30).map { picture("p$it") }
        coEvery { get("x", 1, 30) } returns PicturesPage(2, many)
        coEvery { get("x", 2, 30) } throws HttpException(
            Response.error<Any>(429, ByteArray(0).toResponseBody(null))
        )
        val viewModel = vm("x", get)
        awaitCondition { viewModel.uiState.value is PicturesGridState.Loaded }
        viewModel.loadNextPage()
        awaitCondition { viewModel.uiState.value is PicturesGridState.Error }
        assertEquals("Query Limit Reached", (viewModel.uiState.value as PicturesGridState.Error).message)
    }

    @Test
    fun `onSetWallpaper success updates preview flags`() = runBlocking {
        val get = mockk<GetPicturesUseCase>()
        val setW = mockk<SetWallpaperUseCase>()
        coEvery { get("x", 1, 30) } returns PicturesPage(1, listOf(picture("r1")))
        coEvery { setW.setWallpaper("full_r1") } returns Result.success(Unit)
        val viewModel = vm("x", get, setW)
        awaitCondition { viewModel.uiState.value is PicturesGridState.Loaded }
        val pr = preview("r1")
        viewModel.onPicturePreview(pr)
        viewModel.onSetWallpaper("full_r1")
        awaitCondition {
            val st = viewModel.uiState.value as? PicturesGridState.Loaded
            st?.preview?.isWallpaperSet == true && st.preview?.settingWallpaper == false
        }
    }

    @Test
    fun `onSetWallpaper failure shows error`() = runBlocking {
        val get = mockk<GetPicturesUseCase>()
        val setW = mockk<SetWallpaperUseCase>()
        coEvery { get("x", 1, 30) } returns PicturesPage(1, listOf(picture("r1")))
        coEvery { setW.setWallpaper(any()) } returns Result.failure(RuntimeException("e"))
        val viewModel = vm("x", get, setW)
        awaitCondition { viewModel.uiState.value is PicturesGridState.Loaded }
        viewModel.onSetWallpaper("u")
        awaitCondition { viewModel.uiState.value is PicturesGridState.Error }
    }

    @Test
    fun `onPicturePreview only when loaded`() = runBlocking {
        val get = mockk<GetPicturesUseCase>()
        coEvery { get("x", 1, 30) } throws HttpException(
            Response.error<Any>(500, ByteArray(0).toResponseBody(null))
        )
        val viewModel = vm("x", get)
        awaitCondition { viewModel.uiState.value is PicturesGridState.Error }
        viewModel.onPicturePreview(preview("x"))
        assertTrue(viewModel.uiState.value is PicturesGridState.Error)
    }

    @Test
    fun `onDismissPreview clears`() = runBlocking {
        val get = mockk<GetPicturesUseCase>()
        coEvery { get("x", 1, 30) } returns PicturesPage(1, listOf(picture("r1")))
        val viewModel = vm("x", get)
        awaitCondition { viewModel.uiState.value is PicturesGridState.Loaded }
        viewModel.onPicturePreview(preview("r1"))
        awaitCondition { (viewModel.uiState.value as PicturesGridState.Loaded).preview != null }
        viewModel.onDismissPreview()
        assertNull((viewModel.uiState.value as PicturesGridState.Loaded).preview)
    }

    @Test
    fun `double tap adds to favorites`() = runBlocking {
        val get = mockk<GetPicturesUseCase>()
        val add = mockk<AddToFavoritesUseCase>(relaxed = true)
        coEvery { get("topic", 1, 30) } returns PicturesPage(1, listOf(picture("r1")))
        val viewModel = vm("topic", get, add = add)
        awaitCondition { viewModel.uiState.value is PicturesGridState.Loaded }
        val pr = preview("r1")
        viewModel.onPreviewPictureDoubleTap(pr)
        delay(100)
        coVerify {
            add(
                FavoritePicture(
                    previewUrl = pr.previewUrl,
                    fullPicUrl = pr.fullPictureUrl,
                    smallUrl = pr.smallUrl,
                    thumbUrl = pr.thumbUrl,
                    topic = "topic"
                )
            )
        }
    }

    @Test
    fun `loadNextPage is no op when not loaded`() = runBlocking {
        val get = mockk<GetPicturesUseCase>()
        coEvery { get("x", 1, 30) } coAnswers { delay(5000); PicturesPage(1, emptyList()) }
        val viewModel = vm("x", get)
        viewModel.loadNextPage()
        coVerify(exactly = 1) { get("x", 1, 30) }
    }

    private fun preview(reg: String) = PreviewState(
        thumbUrl = "t_$reg",
        smallUrl = "s_$reg",
        previewUrl = "p_$reg",
        fullPictureUrl = "full_$reg"
    )
}
