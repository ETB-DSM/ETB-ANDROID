package com.aicane.app.navigation

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationCollector @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun startUpdates(onLocation: (Location) -> Unit): LocationCallback {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L)
            .setMinUpdateIntervalMillis(1000L)
            .build()
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let(onLocation)
            }
        }
        fusedClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
        return callback
    }

    fun stopUpdates(callback: LocationCallback) {
        fusedClient.removeLocationUpdates(callback)
    }
}
