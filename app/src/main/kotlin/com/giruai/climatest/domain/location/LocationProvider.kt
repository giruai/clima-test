package com.giruai.climatest.domain.location

data class Location(
    val latitude: Double,
    val longitude: Double
)

interface LocationProvider {
    suspend fun getCurrentLocation(): Result<Location>
    fun hasPermission(): Boolean
}
