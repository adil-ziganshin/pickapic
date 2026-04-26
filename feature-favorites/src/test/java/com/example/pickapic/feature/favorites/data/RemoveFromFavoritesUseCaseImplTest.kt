package com.example.pickapic.feature.favorites.data

import com.gsgroup.feature_favorites_api.entity.FavoritePicture
import io.mockk.coJustRun
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException

class RemoveFromFavoritesUseCaseImplTest {

    private fun samplePicture(
        p: String = "p",
        f: String = "f",
        s: String = "s",
        t: String = "t"
    ) = FavoritePicture(
        previewUrl = p,
        fullPicUrl = f,
        smallUrl = s,
        thumbUrl = t,
        topic = "x"
    )

    @Test
    fun `delegates to repository`() = runTest {
        val picture = samplePicture()
        val repository = mockk<FavoritePicturesRepository>()
        coJustRun { repository.removeFromFavorites(picture) }
        val useCase = RemoveFromFavoritesUseCaseImpl(repository)
        useCase(picture = picture)
        coVerify(exactly = 1) { repository.removeFromFavorites(picture) }
    }

    @Test
    fun `second remove calls repository again`() = runTest {
        val a = samplePicture("a1", "a2", "a3", "a4")
        val b = samplePicture("b1", "b2", "b3", "b4")
        val repository = mockk<FavoritePicturesRepository>()
        coJustRun { repository.removeFromFavorites(a) }
        coJustRun { repository.removeFromFavorites(b) }
        val useCase = RemoveFromFavoritesUseCaseImpl(repository)
        useCase(a)
        useCase(b)
        coVerify(exactly = 1) { repository.removeFromFavorites(a) }
        coVerify(exactly = 1) { repository.removeFromFavorites(b) }
    }

    @Test
    fun `propagates exception from repository`() = runTest {
        val picture = samplePicture()
        val repository = mockk<FavoritePicturesRepository>()
        val err = IOException("db")
        coEvery { repository.removeFromFavorites(picture) } throws err
        val useCase = RemoveFromFavoritesUseCaseImpl(repository)
        var caught: Throwable? = null
        try {
            useCase(picture)
        } catch (e: IOException) {
            caught = e
        }
        assertEquals(err, caught)
    }
}
