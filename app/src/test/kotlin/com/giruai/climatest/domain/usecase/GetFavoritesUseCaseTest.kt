package com.giruai.climatest.domain.usecase

import com.giruai.climatest.domain.model.City
import com.giruai.climatest.domain.repository.FavoritesRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetFavoritesUseCaseTest {

    private lateinit var repository: FavoritesRepository
    private lateinit var useCase: GetFavoritesUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetFavoritesUseCase(repository)
    }

    @Test
    fun `invoke returns Flow of favorites from repository`() = runTest {
        // Given
        val favorites = listOf(
            City(1, "Paris", "France", 48.8566, 2.3522),
            City(2, "London", "UK", 51.5074, -0.1278)
        )
        every { repository.getFavorites() } returns flowOf(favorites)

        // When
        val result = useCase().first()

        // Then
        assertEquals(2, result.size)
        assertEquals("Paris", result[0].name)
        assertEquals("London", result[1].name)
        verify(exactly = 1) { repository.getFavorites() }
    }

    @Test
    fun `invoke returns empty list when no favorites`() = runTest {
        // Given
        every { repository.getFavorites() } returns flowOf(emptyList())

        // When
        val result = useCase().first()

        // Then
        assertTrue(result.isEmpty())
    }
}
