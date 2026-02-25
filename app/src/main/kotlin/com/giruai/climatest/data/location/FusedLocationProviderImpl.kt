package com.giruai.climatest.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.giruai.climatest.domain.location.Location
import com.giruai.climatest.domain.location.LocationProvider
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FusedLocationProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationProvider {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    override suspend fun getCurrentLocation(): Result<Location> {
        if (!hasPermission()) {
            Timber.w("Location permission not granted")
            return Result.failure(SecurityException("Location permission not granted"))
        }

        val cancellationToken = CancellationTokenSource()
        return try {
            withTimeout(LOCATION_TIMEOUT_MS) {
                @Suppress("MissingPermission")
                val androidLocation = fusedClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    cancellationToken.token
                ).await()

                if (androidLocation != null) {
                    Timber.d("Location obtained: ${androidLocation.latitude}, ${androidLocation.longitude}")
                    Result.success(
                        Location(
                            latitude = androidLocation.latitude,
                            longitude = androidLocation.longitude
                        )
                    )
                } else {
                    Timber.w("Location was null, trying last known location")
                    getLastKnownLocation()
                }
            }
        } catch (e: TimeoutCancellationException) {
            Timber.w("Location timeout after ${LOCATION_TIMEOUT_MS}ms, trying last known location")
            getLastKnownLocationOrFallback()
        } catch (e: Exception) {
            Timber.e(e, "Failed to get location")
            Result.failure(e)
        } finally {
            cancellationToken.cancel()
        }
    }

    @Suppress("MissingPermission")
    private suspend fun getLastKnownLocation(): Result<Location> {
        return try {
            val lastLocation = fusedClient.lastLocation.await()
            if (lastLocation != null) {
                Timber.d("Using last known location: ${lastLocation.latitude}, ${lastLocation.longitude}")
                Result.success(
                    Location(
                        latitude = lastLocation.latitude,
                        longitude = lastLocation.longitude
                    )
                )
            } else {
                Result.failure(Exception("No location available. Please enable location services."))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get last known location")
            Result.failure(e)
        }
    }

    @Suppress("MissingPermission")
    private suspend fun getLastKnownLocationOrFallback(): Result<Location> {
        val lastKnown = getLastKnownLocation()
        if (lastKnown.isSuccess) return lastKnown

        // Fallback to Buenos Aires when all location methods fail
        Timber.w("All location methods failed, using fallback: Buenos Aires")
        return Result.success(FALLBACK_LOCATION)
    }

    override fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val LOCATION_TIMEOUT_MS = 30_000L

        // Buenos Aires fallback when GPS and lastLocation both fail
        private val FALLBACK_LOCATION = Location(
            latitude = -34.6037,
            longitude = -58.3816
        )
    }
}
