package com.giruai.climatest.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.giruai.climatest.data.local.dao.FavoriteCityDao
import com.giruai.climatest.data.local.entity.FavoriteCityEntity

@Database(
    entities = [FavoriteCityEntity::class],
    version = 1,
    exportSchema = true
)
abstract class ClimaDatabase : RoomDatabase() {
    abstract fun favoriteCityDao(): FavoriteCityDao
}
