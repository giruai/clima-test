package com.giruai.climatest.domain.usecase

import com.giruai.climatest.domain.model.City
import com.giruai.climatest.domain.repository.FavoritesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AddFavoriteUseCaseTest {

    private lateinit var repository: FavoritesRepository
    private lateinit var useCase: AddFavoriteUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = AddFavoriteUseCase(repository)
    }

    @Test
    fun `invoke adds city to favorites`() = runTest {
        // Given
        val city = City(1, "Paris", "France", 48.8566, 2.3522)
        coEvery { repository.addFavorite(city) } returns Result.success(Unit)

        // When
        val result = useCase(city)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.addFavorite(city) }
    }

    @Test
    fun `invoke propagates repository failure`() = runTest {
        // Given
        val city = City(1, "Paris", "France", 48.8566, 2.3522)
        val exception = IllegalStateException("Maximum 10 favorites allowed")
        coEvery { repository.addFavorite(city) } returns Result.failure(exception)

        // When
        val result = useCase(city)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke with limit exceeded returns failure`() = runTest {
        // Given
        val city = City(11, "Tokyo", "Japan", 35.6762, 139.6503)
        coEvery { repository.addFavorite(city) } returns Result.failure(
            IllegalStateException("Maximum 10 favorites allowed")
        )

        // When
        val result = useCase(city)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("10") == true)
    }
}
