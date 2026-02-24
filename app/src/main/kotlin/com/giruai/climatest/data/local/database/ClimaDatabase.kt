package com.giruai.climatest.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteCityEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ClimaDatabase : RoomDatabase() {
    abstract fun favoriteCityDao(): FavoriteCityDao
}
