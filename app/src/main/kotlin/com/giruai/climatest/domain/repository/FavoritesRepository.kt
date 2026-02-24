package com.giruai.climatest.domain.repository

import com.giruai.climatest.domain.model.City
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getAll(): Flow<List<City>>
    suspend fun add(city: City)
    suspend fun remove(cityId: Long)
    suspend fun count(): Int
    suspend fun isFavorite(cityId: Long): Boolean
}
