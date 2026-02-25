package com.giruai.climatest.domain.usecase

import com.giruai.climatest.domain.repository.FavoritesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class IsFavoriteUseCaseTest {

    private lateinit var repository: FavoritesRepository
    private lateinit var useCase: IsFavoriteUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = IsFavoriteUseCase(repository)
    }

    @Test
    fun `invoke returns true when city is favorited`() = runTest {
        // Given
        val cityId = 1L
        coEvery { repository.isFavorite(cityId) } returns true

        // When
        val result = useCase(cityId)

        // Then
        assertTrue(result)
        coVerify(exactly = 1) { repository.isFavorite(cityId) }
    }

    @Test
    fun `invoke returns false when city is not favorited`() = runTest {
        // Given
        val cityId = 1L
        coEvery { repository.isFavorite(cityId) } returns false

        // When
        val result = useCase(cityId)

        // Then
        assertFalse(result)
    }
}
