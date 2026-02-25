package com.giruai.climatest.data.repository

import com.giruai.climatest.data.local.dao.FavoriteCityDao
import com.giruai.climatest.data.local.entity.FavoriteCityEntity
import com.giruai.climatest.domain.model.City
import com.giruai.climatest.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val favoriteCityDao: FavoriteCityDao
) : FavoritesRepository {

    override fun getFavorites(): Flow<List<City>> {
        return favoriteCityDao.getAllFavorites().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getFavoriteById(cityId: Long): City? {
        return favoriteCityDao.getFavoriteById(cityId)?.toDomain()
    }

    override suspend fun addFavorite(city: City): Result<Unit> {
        return try {
            val currentCount = favoriteCityDao.getFavoriteCount()
            if (currentCount >= MAX_FAVORITES) {
                Timber.w("Cannot add favorite: limit of $MAX_FAVORITES reached")
                return Result.failure(
                    IllegalStateException("Maximum $MAX_FAVORITES favorites allowed")
                )
            }

            val entity = city.toEntity()
            favoriteCityDao.insertFavorite(entity)
            Timber.d("Added favorite: ${city.name}")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to add favorite: ${city.name}")
            Result.failure(e)
        }
    }

    override suspend fun removeFavorite(cityId: Long): Result<Unit> {
        return try {
            favoriteCityDao.deleteFavorite(cityId)
            Timber.d("Removed favorite: cityId=$cityId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to remove favorite: cityId=$cityId")
            Result.failure(e)
        }
    }

    override suspend fun getFavoriteCount(): Int {
        return favoriteCityDao.getFavoriteCount()
    }

    override suspend fun isFavorite(cityId: Long): Boolean {
        return favoriteCityDao.getFavoriteById(cityId) != null
    }

    private fun FavoriteCityEntity.toDomain(): City {
        return City(
            id = id,
            name = name,
            country = country,
            latitude = latitude,
            longitude = longitude
        )
    }

    private fun City.toEntity(): FavoriteCityEntity {
        return FavoriteCityEntity(
            id = id,
            name = name,
            country = country,
            latitude = latitude,
            longitude = longitude,
            addedAt = System.currentTimeMillis()
        )
    }

    companion object {
        private const val MAX_FAVORITES = 10
    }
}
