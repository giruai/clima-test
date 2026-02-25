package com.giruai.climatest.domain.repository

import com.giruai.climatest.domain.model.City
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    
    /**
     * Get all favorite cities as a Flow.
     * Emits new list whenever favorites change.
     */
    fun getFavorites(): Flow<List<City>>
    
    /**
     * Get a single favorite city by ID.
     * Returns null if not found.
     */
    suspend fun getFavoriteById(cityId: Long): City?
    
    /**
     * Add a city to favorites.
     * Returns success/failure result with error message if limit exceeded.
     */
    suspend fun addFavorite(city: City): Result<Unit>
    
    /**
     * Remove a city from favorites by ID.
     */
    suspend fun removeFavorite(cityId: Long): Result<Unit>
    
    /**
     * Get current count of favorites.
     */
    suspend fun getFavoriteCount(): Int
    
    /**
     * Check if a city is already favorited.
     */
    suspend fun isFavorite(cityId: Long): Boolean
}
