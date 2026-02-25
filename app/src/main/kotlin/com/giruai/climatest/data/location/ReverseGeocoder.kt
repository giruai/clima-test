package com.giruai.climatest.data.location

import android.content.Context
import android.location.Geocoder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReverseGeocoder @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val cache = mutableMapOf<String, String>()

    suspend fun getCityName(latitude: Double, longitude: Double): String {
        val key = "%.2f,%.2f".format(latitude, longitude)

        // Check cache first
        cache[key]?.let { return it }

        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                val address = addresses?.firstOrNull()

                // Pick best available name, preferring human-readable city names
                val bestName = when {
                    address == null -> null
                    else -> {
                        val locality = address.locality
                        val subAdmin = address.subAdminArea
                        val admin = address.adminArea
                        val country = address.countryCode

                        // Filter out non-city-name values
                        val isPostalCode = locality != null && locality.matches(Regex("^[A-Z]?\\d.*"))
                        val isGenericDistrict = subAdmin != null && subAdmin.matches(Regex("^(Comuna|District|Ward|Sector)\\s.*", RegexOption.IGNORE_CASE))

                        val cityPart = when {
                            locality != null && !isPostalCode -> locality
                            subAdmin != null && !isGenericDistrict -> subAdmin
                            admin != null -> admin
                            subAdmin != null -> subAdmin  // fallback to generic district
                            locality != null -> locality  // fallback to postal code
                            else -> null
                        }

                        if (cityPart != null && country != null) "$cityPart, $country"
                        else if (cityPart != null) cityPart
                        else address.countryName
                    }
                }

                val cityName = bestName

                cityName?.also { cache[key] = it }
                    ?: formatCoordinates(latitude, longitude)
            } catch (e: Exception) {
                Timber.w(e, "Reverse geocoding failed for $key")
                formatCoordinates(latitude, longitude)
            }
        }
    }

    private fun formatCoordinates(latitude: Double, longitude: Double): String {
        return "%.2f°, %.2f°".format(latitude, longitude)
    }
}
