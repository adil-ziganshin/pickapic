package com.example.pickapic.feature.pictures.data

import com.example.pickapic.feature.pictures.domain.Picture
import com.example.pickapic.feature.pictures.domain.PicturesPage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class GetPicturesUseCaseImplTest {

    @Test
    fun `returns page from repository`() = runTest {
        val page = PicturesPage(
            totalPages = 2,
            results = listOf(
                Picture("r", "f", "reg1", "s", "t")
            )
        )
        val repository = mockk<PictureRepository>()
        coEvery { repository.getData("cats", 1, 30) } returns page
        val useCase = GetPicturesUseCaseImpl(repository)
        assertEquals(page, useCase(query = "cats", page = 1, perPage = 30))
        coVerify(exactly = 1) { repository.getData("cats", 1, 30) }
    }

    @Test
    fun `forwards other query page and perPage`() = runTest {
        val page = PicturesPage(5, emptyList())
        val repository = mockk<PictureRepository>()
        coEvery { repository.getData("dogs", 4, 15) } returns page
        val useCase = GetPicturesUseCaseImpl(repository)
        assertEquals(page, useCase("dogs", 4, 15))
    }

    @Test
    fun `empty results are returned`() = runTest {
        val page = PicturesPage(1, emptyList())
        val repository = mockk<PictureRepository>()
        coEvery { repository.getData("q", 1, 30) } returns page
        val useCase = GetPicturesUseCaseImpl(repository)
        assertTrue(useCase("q", 1, 30).results.isEmpty())
    }

    @Test
    fun `propagates errors from repository`() = runTest {
        val repository = mockk<PictureRepository>()
        val error = IOException("io")
        coEvery { repository.getData("x", 1, 30) } throws error
        val useCase = GetPicturesUseCaseImpl(repository)
        var caught: Throwable? = null
        try {
            useCase("x", 1, 30)
        } catch (e: IOException) {
            caught = e
        }
        assertEquals(error, caught)
    }

    @Test
    fun `multiple pages call repository with different page numbers`() = runTest {
        val p1 = PicturesPage(3, listOf(Picture("r", "f", "a", "s", "t")))
        val p2 = PicturesPage(3, listOf(Picture("r2", "f2", "b", "s2", "t2")))
        val repository = mockk<PictureRepository>()
        coEvery { repository.getData("k", 1, 30) } returns p1
        coEvery { repository.getData("k", 2, 30) } returns p2
        val useCase = GetPicturesUseCaseImpl(repository)
        assertEquals(p1, useCase("k", 1, 30))
        assertEquals(p2, useCase("k", 2, 30))
    }
}
