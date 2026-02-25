package com.giruai.climatest.domain.usecase

import com.giruai.climatest.domain.repository.FavoritesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RemoveFavoriteUseCaseTest {

    private lateinit var repository: FavoritesRepository
    private lateinit var useCase: RemoveFavoriteUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = RemoveFavoriteUseCase(repository)
    }

    @Test
    fun `invoke removes favorite by cityId`() = runTest {
        // Given
        val cityId = 1L
        coEvery { repository.removeFavorite(cityId) } returns Result.success(Unit)

        // When
        val result = useCase(cityId)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.removeFavorite(cityId) }
    }

    @Test
    fun `invoke propagates repository failure`() = runTest {
        // Given
        val cityId = 1L
        val exception = Exception("Database error")
        coEvery { repository.removeFavorite(cityId) } returns Result.failure(exception)

        // When
        val result = useCase(cityId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
