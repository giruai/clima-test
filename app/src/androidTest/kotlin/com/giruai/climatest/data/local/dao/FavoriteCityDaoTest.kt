package com.giruai.climatest.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.giruai.climatest.data.local.database.ClimaDatabase
import com.giruai.climatest.data.local.entity.FavoriteCityEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteCityDaoTest {

    private lateinit var database: ClimaDatabase
    private lateinit var dao: FavoriteCityDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            ClimaDatabase::class.java
        ).build()
        dao = database.favoriteCityDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertFavorite_andRetrieve() = runTest {
        // Given
        val city = FavoriteCityEntity(
            id = 1,
            name = "Paris",
            country = "France",
            latitude = 48.8566,
            longitude = 2.3522,
            addedAt = System.currentTimeMillis()
        )

        // When
        dao.insertFavorite(city)

        // Then
        val favorites = dao.getAllFavorites().first()
        assertEquals(1, favorites.size)
        assertEquals("Paris", favorites[0].name)
        assertEquals("France", favorites[0].country)
    }

    @Test
    fun insertMultipleFavorites_orderedByAddedAtDesc() = runTest {
        // Given
        val paris = FavoriteCityEntity(1, "Paris", "France", 48.8566, 2.3522, 1000L)
        val london = FavoriteCityEntity(2, "London", "UK", 51.5074, -0.1278, 2000L)
        val tokyo = FavoriteCityEntity(3, "Tokyo", "Japan", 35.6762, 139.6503, 3000L)

        // When
        dao.insertFavorite(paris)
        dao.insertFavorite(london)
        dao.insertFavorite(tokyo)

        // Then
        val favorites = dao.getAllFavorites().first()
        assertEquals(3, favorites.size)
        assertEquals("Tokyo", favorites[0].name) // Most recent
        assertEquals("London", favorites[1].name)
        assertEquals("Paris", favorites[2].name) // Oldest
    }

    @Test
    fun deleteFavorite_removesCity() = runTest {
        // Given
        val city = FavoriteCityEntity(1, "Paris", "France", 48.8566, 2.3522, System.currentTimeMillis())
        dao.insertFavorite(city)

        // When
        dao.deleteFavorite(1)

        // Then
        val favorites = dao.getAllFavorites().first()
        assertTrue(favorites.isEmpty())
    }

    @Test
    fun getFavoriteCount_returnsCorrectCount() = runTest {
        // Given
        val paris = FavoriteCityEntity(1, "Paris", "France", 48.8566, 2.3522, System.currentTimeMillis())
        val london = FavoriteCityEntity(2, "London", "UK", 51.5074, -0.1278, System.currentTimeMillis())

        // When
        dao.insertFavorite(paris)
        dao.insertFavorite(london)

        // Then
        val count = dao.getFavoriteCount()
        assertEquals(2, count)
    }

    @Test
    fun getFavoriteById_returnsCity() = runTest {
        // Given
        val city = FavoriteCityEntity(1, "Paris", "France", 48.8566, 2.3522, System.currentTimeMillis())
        dao.insertFavorite(city)

        // When
        val retrieved = dao.getFavoriteById(1)

        // Then
        assertNotNull(retrieved)
        assertEquals("Paris", retrieved?.name)
    }

    @Test
    fun getFavoriteById_nonExistent_returnsNull() = runTest {
        // When
        val retrieved = dao.getFavoriteById(999)

        // Then
        assertNull(retrieved)
    }

    @Test
    fun insertDuplicate_replacesExisting() = runTest {
        // Given
        val city1 = FavoriteCityEntity(1, "Paris", "France", 48.8566, 2.3522, 1000L)
        val city2 = FavoriteCityEntity(1, "Paris Updated", "France", 48.8566, 2.3522, 2000L)

        // When
        dao.insertFavorite(city1)
        dao.insertFavorite(city2)

        // Then
        val favorites = dao.getAllFavorites().first()
        assertEquals(1, favorites.size)
        assertEquals("Paris Updated", favorites[0].name)
        assertEquals(2000L, favorites[0].addedAt)
    }

    @Test
    fun deleteAll_removesAllCities() = runTest {
        // Given
        dao.insertFavorite(FavoriteCityEntity(1, "Paris", "France", 48.8566, 2.3522, System.currentTimeMillis()))
        dao.insertFavorite(FavoriteCityEntity(2, "London", "UK", 51.5074, -0.1278, System.currentTimeMillis()))

        // When
        dao.deleteAll()

        // Then
        val favorites = dao.getAllFavorites().first()
        assertTrue(favorites.isEmpty())
        assertEquals(0, dao.getFavoriteCount())
    }
}
