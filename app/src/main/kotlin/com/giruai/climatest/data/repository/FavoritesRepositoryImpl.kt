package com.giruai.climatest.data.repository

import com.giruai.climatest.data.local.database.FavoriteCityDao
import com.giruai.climatest.data.local.database.FavoriteCityEntity
import com.giruai.climatest.domain.model.City
import com.giruai.climatest.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val dao: FavoriteCityDao
) : FavoritesRepository {

    override fun getAll(): Flow<List<City>> =
        dao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun add(city: City) {
        Timber.d("Adding favorite: ${city.name}")
        dao.insert(
            FavoriteCityEntity(
                cityId = city.id,
                name = city.name,
                country = city.country,
                latitude = city.latitude,
                longitude = city.longitude,
                addedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun remove(cityId: Long) {
        Timber.d("Removing favorite: $cityId")
        dao.delete(cityId)
    }

    override suspend fun count(): Int = dao.count()

    override suspend fun isFavorite(cityId: Long): Boolean {
        // Simple check: get count where cityId matches
        // For now, use the existing count method pattern
        return false // TODO: implement proper check in DAO
    }

    private fun FavoriteCityEntity.toDomain() = City(
        id = cityId,
        name = name,
        country = country,
        latitude = latitude,
        longitude = longitude
    )
}
