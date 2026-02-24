package com.giruai.climatest.data.remote.dto

data class GeocodingResponse(
    val results: List<GeocodingResultDto>?
)

data class GeocodingResultDto(
    val id: Long,
    val name: String,
    val country: String?,
    val latitude: Double,
    val longitude: Double,
    val admin1: String?
)
