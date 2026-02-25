package com.giruai.climatest.data.local.dao

import androidx.room.*
import com.giruai.climatest.data.local.entity.FavoriteCityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCityDao {

    @Query("SELECT * FROM favorite_cities ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteCityEntity>>

    @Query("SELECT * FROM favorite_cities WHERE id = :cityId LIMIT 1")
    suspend fun getFavoriteById(cityId: Long): FavoriteCityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(city: FavoriteCityEntity)

    @Query("DELETE FROM favorite_cities WHERE id = :cityId")
    suspend fun deleteFavorite(cityId: Long)

    @Query("SELECT COUNT(*) FROM favorite_cities")
    suspend fun getFavoriteCount(): Int

    @Query("DELETE FROM favorite_cities")
    suspend fun deleteAll()
}
