package com.example.pickapic.wallpaper.data

import android.app.WallpaperManager
import android.os.Build
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test

class SetWallpaperUseCaseImplTest {

    private var server: MockWebServer? = null

    @After
    fun tearDown() {
        server?.shutdown()
    }

    @Test
    fun `malformed url returns failure`() = runTest {
        val manager = mockk<WallpaperManager>(relaxed = true)
        val useCase = SetWallpaperUseCaseImpl(manager)
        val result = useCase.setWallpaper("::invalid::")
        assertTrue(result.isFailure)
    }

    @Test
    fun `empty string url returns failure`() = runTest {
        val manager = mockk<WallpaperManager>(relaxed = true)
        val useCase = SetWallpaperUseCaseImpl(manager)
        val result = useCase.setWallpaper("")
        assertTrue(result.isFailure)
    }

    @Test
    fun `unreachable connection returns failure`() = runTest {
        val manager = mockk<WallpaperManager>(relaxed = true)
        val useCase = SetWallpaperUseCaseImpl(manager)
        val result = useCase.setWallpaper("http://127.0.0.1:1/nope")
        assertTrue(result.isFailure)
    }

    @Test
    fun `open stream success and manager succeeds returns success`() = runTest {
        val s = MockWebServer().apply { start() }
        server = s
        s.enqueue(MockResponse().setBody("x".repeat(20)).setResponseCode(200))
        val imageUrl = s.url("/wall.jpg").toString()
        val manager = mockk<WallpaperManager>(relaxed = true)
        val useCase = SetWallpaperUseCaseImpl(manager)
        val result = useCase.setWallpaper(imageUrl)
        assertTrue(result.isSuccess)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            verify { manager.setStream(any(), any(), any(), any()) }
        } else {
            verify { manager.setStream(any()) }
        }
    }

    @Test
    fun `wallpaper manager throws after stream open returns failure`() = runTest {
        val s = MockWebServer().apply { start() }
        server = s
        s.enqueue(MockResponse().setBody("x".repeat(20)).setResponseCode(200))
        val imageUrl = s.url("/p.png").toString()
        val manager = mockk<WallpaperManager>(relaxed = true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            every { manager.setStream(any(), any(), any(), any()) } throws RuntimeException("set failed")
        } else {
            every { manager.setStream(any()) } throws RuntimeException("set failed")
        }
        val useCase = SetWallpaperUseCaseImpl(manager)
        val result = useCase.setWallpaper(imageUrl)
        assertTrue(result.isFailure)
    }
}
