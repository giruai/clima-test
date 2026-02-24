package com.giruai.climatest.presentation.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import timber.log.Timber

object PermissionUtils {
    
    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}

class LocationPermissionHandler(
    private val activity: ComponentActivity,
    private val onPermissionResult: (Boolean) -> Unit
) {
    private var permissionLauncher: ActivityResultLauncher<Array<String>>? = null

    init {
        permissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions.values.any { it }
            Timber.d("Location permission result: $granted")
            onPermissionResult(granted)
        }
    }

    fun requestPermission() {
        Timber.d("Requesting location permission")
        permissionLauncher?.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    fun hasPermission(): Boolean {
        return PermissionUtils.hasLocationPermission(activity)
    }
}

@Composable
fun rememberLocationPermissionHandler(
    onPermissionResult: (Boolean) -> Unit
): LocationPermissionHandler {
    val context = LocalContext.current as ComponentActivity
    return LocationPermissionHandler(context, onPermissionResult)
}
