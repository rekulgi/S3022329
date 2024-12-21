package uk.ac.tees.mad.plasmalink

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import java.util.Locale

class LocationManager(
    private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val settingsClient: SettingsClient = LocationServices.getSettingsClient(context)
    private lateinit var locationCallback: LocationCallback
    val locationPrefs by lazy {
        LocationCache(context)
    }


    @SuppressLint("MissingPermission")
    fun getCurrentLocation(onSuccess: (Location) -> Unit, onError: (String) -> Unit) {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.forEach { location ->
                    getAddressFromCoordinates(
                        location.latitude,
                        location.longitude, {
                            locationPrefs.saveLocation(it ?: "")
                        },
                        {}
                    )
                    onSuccess(location)
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun checkGpsSettings() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(false)
            .setNeedBle(false)

        settingsClient.checkLocationSettings(builder.build())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.locationSettingsStates?.let {
                        Log.e("LocationSettings", it.toString())
                        // TODO: Handle successful location settings check
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("LocationSettings", "Failed to check location settings: ${exception.message}")
                if (exception is ResolvableApiException) {
                    when (exception.statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            Log.d(
                                "LocationSettings",
                                "Attempting to resolve location settings issue."
                            )
                            try {
                                exception.startResolutionForResult(context as Activity, 0)
                            } catch (sendEx: IntentSender.SendIntentException) {
                                Log.d("LocationSettings", "Failed to send resolution request.")
                            }
                        }

                        else -> {
                            // Handle other status codes if needed
                        }
                    }
                }
            }
    }

    fun getAddressFromCoordinates(
        latitude: Double,
        longitude: Double,
        onSuccess: (String?) -> Unit,
        onError: (String) -> Unit
    ) {
        val geocoder = Geocoder(context, Locale.getDefault())

        if (!Geocoder.isPresent()) {
            onError("Geocoder not available")
            return
        }
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            val addressString = addresses?.get(0)?.getAddressLine(0) ?: "Address not found"
            onSuccess(addressString)
        } catch (e: Exception) {
            onError("Error retrieving address: ${e.localizedMessage}")
        }
    }

    companion object {
        private const val ONE_MINUTE: Long = 60_000L
        val locationRequest: LocationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, ONE_MINUTE)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(ONE_MINUTE / 4)
                .setMaxUpdateDelayMillis(ONE_MINUTE / 2)
                .build()
    }
}
