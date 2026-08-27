package com.aicane.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.aicane.app.domain.model.NavigationAction
import com.aicane.app.domain.model.NavigationInstruction
import com.aicane.app.domain.usecase.navigation.FetchRouteUseCase
import com.aicane.app.domain.usecase.navigation.UpdateSessionStatusUseCase
import com.aicane.app.navigation.ActionCalculator
import com.aicane.app.navigation.ActionUploader
import com.aicane.app.navigation.LocationCollector
import com.aicane.app.navigation.NavigationConfig
import com.aicane.app.navigation.NavigationStateHolder
import com.google.android.gms.location.LocationCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NavigationService : Service() {

    @Inject lateinit var locationCollector: LocationCollector
    @Inject lateinit var actionCalculator: ActionCalculator
    @Inject lateinit var actionUploader: ActionUploader
    @Inject lateinit var fetchRouteUseCase: FetchRouteUseCase
    @Inject lateinit var updateStatusUseCase: UpdateSessionStatusUseCase
    @Inject lateinit var stateHolder: NavigationStateHolder
    @Inject lateinit var config: NavigationConfig

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var locationCallback: LocationCallback? = null

    @Volatile private var isRerouting = false

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "navigation_channel"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification())
        actionCalculator.reset()
        startLocationUpdates()
        return START_STICKY
    }

    private fun startLocationUpdates() {
        locationCallback = locationCollector.startUpdates { location ->
            serviceScope.launch {
                val (action, distance) = actionCalculator.calculate(
                    lat          = location.latitude,
                    lng          = location.longitude,
                    steps        = config.steps,
                    destLat      = config.destinationLat,
                    destLng      = config.destinationLng,
                    destRadius   = config.destinationRadius,
                )
                val instruction = NavigationInstruction(
                    sessionId      = config.sessionId,
                    action         = action,
                    distanceMeters = distance,
                    message        = action.toKoreanMessage(distance),
                )
                stateHolder.currentInstruction.value = instruction
                actionUploader.upload(instruction)

                when (action) {
                    NavigationAction.ARRIVED -> {
                        updateStatusUseCase(config.sessionId, "arrived")
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopSelf()
                    }
                    NavigationAction.REROUTE -> handleReroute(location.latitude, location.longitude)
                    else -> {}
                }
            }
        }
    }

    private suspend fun handleReroute(lat: Double, lng: Double) {
        if (isRerouting) return
        isRerouting = true
        fetchRouteUseCase(lat, lng, config.destinationLat, config.destinationLng)
            .onSuccess { newSteps ->
                config.steps = newSteps
                actionUploader.reset()
            }
        isRerouting = false
    }

    override fun onDestroy() {
        locationCallback?.let { locationCollector.stopUpdates(it) }
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID, "길안내", NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("길안내 진행 중")
        .setSmallIcon(android.R.drawable.ic_menu_directions)
        .setOngoing(true)
        .build()
}

private fun NavigationAction.toKoreanMessage(distance: Double): String = when (this) {
    NavigationAction.STRAIGHT      -> "직진"
    NavigationAction.PREPARE_LEFT  -> "${distance.toInt()}m 앞 좌회전"
    NavigationAction.LEFT          -> "좌회전"
    NavigationAction.PREPARE_RIGHT -> "${distance.toInt()}m 앞 우회전"
    NavigationAction.RIGHT         -> "우회전"
    NavigationAction.ARRIVED       -> "목적지 도착"
    NavigationAction.REROUTE       -> "경로 재탐색 중"
}
