package com.giruai.climatest.domain.usecase

import com.giruai.climatest.domain.model.City
import com.giruai.climatest.domain.repository.WeatherRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchCitiesUseCaseTest {

    private lateinit var repository: WeatherRepository
    private lateinit var useCase: SearchCitiesUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = SearchCitiesUseCase(repository)
    }

    @Test
    fun `invoke with query less than 2 chars returns empty list`() = runTest {
        // Given
        val query = "P"

        // When
        val result = useCase(query)

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
        coVerify(exactly = 0) { repository.searchCities(any()) }
    }

    @Test
    fun `invoke with empty query returns empty list`() = runTest {
        // Given
        val query = ""

        // When
        val result = useCase(query)

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
        coVerify(exactly = 0) { repository.searchCities(any()) }
    }

    @Test
    fun `invoke with valid query calls repository`() = runTest {
        // Given
        val query = "Paris"
        val expectedCities = listOf(
            City(1, "Paris", "France", 48.8566, 2.3522)
        )
        coEvery { repository.searchCities(query) } returns Result.success(expectedCities)

        // When
        val result = useCase(query)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedCities, result.getOrNull())
        coVerify(exactly = 1) { repository.searchCities(query) }
    }

    @Test
    fun `invoke propagates repository failure`() = runTest {
        // Given
        val query = "Paris"
        val exception = Exception("Network error")
        coEvery { repository.searchCities(query) } returns Result.failure(exception)

        // When
        val result = useCase(query)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke trims query to 2 chars minimum`() = runTest {
        // Given
        val validQuery = "Pa"
        val expectedCities = listOf(
            City(1, "Paris", "France", 48.8566, 2.3522),
            City(2, "Panama City", "Panama", 8.9824, -79.5199)
        )
        coEvery { repository.searchCities(validQuery) } returns Result.success(expectedCities)

        // When
        val result = useCase(validQuery)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        coVerify(exactly = 1) { repository.searchCities(validQuery) }
    }
}
