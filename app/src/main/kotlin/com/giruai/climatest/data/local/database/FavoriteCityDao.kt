package com.giruai.climatest.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCityDao {

    @Query("SELECT * FROM favorite_cities ORDER BY addedAt ASC")
    fun getAll(): Flow<List<FavoriteCityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: FavoriteCityEntity)

    @Query("DELETE FROM favorite_cities WHERE cityId = :cityId")
    suspend fun delete(cityId: Long)

    @Query("SELECT COUNT(*) FROM favorite_cities")
    suspend fun count(): Int
}
