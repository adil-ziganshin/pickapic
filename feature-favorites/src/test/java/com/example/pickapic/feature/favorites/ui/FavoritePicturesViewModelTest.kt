package com.example.pickapic.feature.favorites.ui

import android.util.Log
import com.example.pickapic.feature.favorites.MainDispatcherRule
import com.example.pickapic.feature.favorites.R
import com.example.pickapic.feature.favorites.data.FavoritePicturesRepository
import com.example.pickapic.uikit.pictures.PicturesGridState
import com.example.pickapic.uikit.pictures.PreviewState
import com.example.pickapic.wallpaper.domain.SetWallpaperUseCase
import com.gsgroup.feature_favorites_api.entity.FavoritePicture
import com.gsgroup.feature_favorites_api.usecase.RemoveFromFavoritesUseCase
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.util.concurrent.TimeoutException

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritePicturesViewModelTest {

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

    private fun fav(
        prev: String = "prev",
        full: String = "full",
        sm: String = "sm",
        th: String = "th"
    ) = FavoritePicture(
        thumbUrl = th,
        previewUrl = prev,
        fullPicUrl = full,
        smallUrl = sm,
        topic = "tp"
    )

    private suspend fun awaitCondition(timeoutMs: Long = 5000, block: () -> Boolean) {
        val end = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < end) {
            if (block()) return
            delay(20)
        }
        if (!block()) throw TimeoutException("timeout")
    }

    private fun context() = mockk<android.content.Context> {
        every { getString(R.string.fav_title) } returns "Fav title"
    }

    @Test
    fun `no favorites shows empty`() = runBlocking {
        val flow = MutableStateFlow<List<FavoritePicture>>(emptyList())
        val repo = mockk<FavoritePicturesRepository>()
        every { repo.fetchPictures() } returns flow
        val remove = mockk<RemoveFromFavoritesUseCase>()
        coJustRun { remove(any()) }
        val setW = mockk<SetWallpaperUseCase> { coEvery { setWallpaper(any()) } returns Result.success(Unit) }
        val vm = FavoritePicturesViewModel(context(), repo, remove, setW)
        awaitCondition { vm.uiState.value is PicturesGridState.Empty }
    }

    @Test
    fun `favorites list shows loaded`() = runBlocking {
        val item = fav("p1", "f1", "s1", "t1")
        val flow = MutableStateFlow(listOf(item))
        val repo = mockk<FavoritePicturesRepository>()
        every { repo.fetchPictures() } returns flow
        val remove = mockk<RemoveFromFavoritesUseCase>()
        coJustRun { remove(any()) }
        val setW = mockk<SetWallpaperUseCase> { coEvery { setWallpaper(any()) } returns Result.success(Unit) }
        val vm = FavoritePicturesViewModel(context(), repo, remove, setW)
        awaitCondition { vm.uiState.value is PicturesGridState.Loaded }
        val st = vm.uiState.value as PicturesGridState.Loaded
        assertEquals(1, st.data.pictures.size)
        assertEquals("p1", st.data.pictures[0].regularUrl)
    }

    @Test
    fun `onSetWallpaper success sets flags on preview`() = runBlocking {
        val item = fav()
        val flow = MutableStateFlow(listOf(item))
        val repo = mockk<FavoritePicturesRepository>()
        every { repo.fetchPictures() } returns flow
        val remove = mockk<RemoveFromFavoritesUseCase>()
        coJustRun { remove(any()) }
        val setW = mockk<SetWallpaperUseCase> { coEvery { setWallpaper("full") } returns Result.success(Unit) }
        val vm = FavoritePicturesViewModel(context(), repo, remove, setW)
        awaitCondition { vm.uiState.value is PicturesGridState.Loaded }
        val pr = previewForItem(item)
        vm.onPicturePreview(pr)
        awaitCondition { (vm.uiState.value as? PicturesGridState.Loaded)?.preview != null }
        vm.onSetWallpaper("full")
        awaitCondition {
            val st = vm.uiState.value as? PicturesGridState.Loaded
            st?.preview?.isWallpaperSet == true
        }
    }

    @Test
    fun `onSetWallpaper failure shows error and clears preview`() = runBlocking {
        val item = fav()
        val flow = MutableStateFlow(listOf(item))
        val repo = mockk<FavoritePicturesRepository>()
        every { repo.fetchPictures() } returns flow
        val remove = mockk<RemoveFromFavoritesUseCase>()
        coJustRun { remove(any()) }
        val setW = mockk<SetWallpaperUseCase> {
            coEvery { setWallpaper(any()) } returns Result.failure(RuntimeException("wall-err"))
        }
        val vm = FavoritePicturesViewModel(context(), repo, remove, setW)
        awaitCondition { vm.uiState.value is PicturesGridState.Loaded }
        vm.onPicturePreview(previewForItem(item))
        vm.onSetWallpaper("url")
        awaitCondition { vm.uiState.value is PicturesGridState.Error }
        assertEquals("wall-err", (vm.uiState.value as PicturesGridState.Error).message)
    }

    @Test
    fun `onErrorDismiss clears error`() = runBlocking {
        val item = fav()
        val flow = MutableStateFlow(listOf(item))
        val repo = mockk<FavoritePicturesRepository>()
        every { repo.fetchPictures() } returns flow
        val remove = mockk<RemoveFromFavoritesUseCase>()
        coJustRun { remove(any()) }
        val setW = mockk<SetWallpaperUseCase> {
            coEvery { setWallpaper(any()) } returns Result.failure(RuntimeException("e"))
        }
        val vm = FavoritePicturesViewModel(context(), repo, remove, setW)
        awaitCondition { vm.uiState.value is PicturesGridState.Loaded }
        vm.onSetWallpaper("u")
        awaitCondition { vm.uiState.value is PicturesGridState.Error }
        vm.onErrorDismiss()
        awaitCondition { vm.uiState.value is PicturesGridState.Loaded }
    }

    @Test
    fun `long click removes favorite`() = runBlocking {
        val item = fav("pa", "fa", "sa", "ta")
        val flow = MutableStateFlow(listOf(item))
        val repo = mockk<FavoritePicturesRepository>()
        every { repo.fetchPictures() } returns flow
        val remove = mockk<RemoveFromFavoritesUseCase>()
        coJustRun { remove(any()) }
        val setW = mockk<SetWallpaperUseCase> { coEvery { setWallpaper(any()) } returns Result.success(Unit) }
        val vm = FavoritePicturesViewModel(context(), repo, remove, setW)
        awaitCondition { vm.uiState.value is PicturesGridState.Loaded }
        val ui = (vm.uiState.value as PicturesGridState.Loaded).data.pictures[0]
        vm.onPictureLongClick(ui)
        delay(100)
        coVerify {
            remove(
                FavoritePicture(
                    thumbUrl = ui.thumbUrl,
                    previewUrl = ui.regularUrl,
                    fullPicUrl = ui.fullUrl,
                    smallUrl = ui.smallUrl,
                    topic = ""
                )
            )
        }
    }

    @Test
    fun `double tap removes and clears preview`() = runBlocking {
        val item = fav("pb", "fb", "sb", "tb")
        val flow = MutableStateFlow(listOf(item))
        val repo = mockk<FavoritePicturesRepository>()
        every { repo.fetchPictures() } returns flow
        val remove = mockk<RemoveFromFavoritesUseCase>()
        coJustRun { remove(any()) }
        val setW = mockk<SetWallpaperUseCase> { coEvery { setWallpaper(any()) } returns Result.success(Unit) }
        val vm = FavoritePicturesViewModel(context(), repo, remove, setW)
        awaitCondition { vm.uiState.value is PicturesGridState.Loaded }
        val pr = previewForItem(item)
        vm.onPicturePreview(pr)
        vm.onPreviewPictureDoubleTap(pr)
        delay(100)
        coVerify { remove(any()) }
    }

    @Test
    fun `onPicturePreview no op when not loaded`() = runBlocking {
        val flow = MutableStateFlow<List<FavoritePicture>>(emptyList())
        val repo = mockk<FavoritePicturesRepository>()
        every { repo.fetchPictures() } returns flow
        val remove = mockk<RemoveFromFavoritesUseCase>()
        coJustRun { remove(any()) }
        val setW = mockk<SetWallpaperUseCase> { coEvery { setWallpaper(any()) } returns Result.success(Unit) }
        val vm = FavoritePicturesViewModel(context(), repo, remove, setW)
        awaitCondition { vm.uiState.value is PicturesGridState.Empty }
        vm.onPicturePreview(PreviewState("a", "b", "c", "d"))
        assertTrue(vm.uiState.value is PicturesGridState.Empty)
    }

    @Test
    fun `onDismissPreview clears`() = runBlocking {
        val item = fav()
        val flow = MutableStateFlow(listOf(item))
        val repo = mockk<FavoritePicturesRepository>()
        every { repo.fetchPictures() } returns flow
        val remove = mockk<RemoveFromFavoritesUseCase>()
        coJustRun { remove(any()) }
        val setW = mockk<SetWallpaperUseCase> { coEvery { setWallpaper(any()) } returns Result.success(Unit) }
        val vm = FavoritePicturesViewModel(context(), repo, remove, setW)
        awaitCondition { vm.uiState.value is PicturesGridState.Loaded }
        vm.onPicturePreview(previewForItem(item))
        awaitCondition { (vm.uiState.value as? PicturesGridState.Loaded)?.preview != null }
        vm.onDismissPreview()
        assertNull((vm.uiState.value as PicturesGridState.Loaded).preview)
    }

    private fun previewForItem(f: FavoritePicture) = PreviewState(
        thumbUrl = f.thumbUrl,
        smallUrl = f.smallUrl,
        previewUrl = f.previewUrl,
        fullPictureUrl = f.fullPicUrl
    )
}
