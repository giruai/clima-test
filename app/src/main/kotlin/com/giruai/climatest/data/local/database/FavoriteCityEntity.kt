package com.giruai.climatest.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_cities")
data class FavoriteCityEntity(
    @PrimaryKey val cityId: Long,
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val addedAt: Long,
    val lastFetchedTemp: Float? = null,
    val lastFetchedAt: Long? = null
)
