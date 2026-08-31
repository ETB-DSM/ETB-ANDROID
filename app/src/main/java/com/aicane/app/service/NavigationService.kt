package com.aicane.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.aicane.app.domain.model.NavigationAction
import com.aicane.app.domain.model.NavigationInstruction
import com.aicane.app.domain.model.TurnType
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
    @Volatile private var lastRerouteAt = 0L
    @Volatile private var lastRerouteLat = 0.0
    @Volatile private var lastRerouteLng = 0.0

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
                val guidance = actionCalculator.calculate(
                    lat          = location.latitude,
                    lng          = location.longitude,
                    steps        = config.steps,
                    destLat      = config.destinationLat,
                    destLng      = config.destinationLng,
                    destRadius   = config.destinationRadius,
                )
                val instruction = NavigationInstruction(
                    sessionId          = config.sessionId,
                    action             = guidance.action,
                    distanceMeters     = guidance.distanceToDest,
                    message            = guidance.action.toKoreanMessage(guidance.distanceToNextTurn, guidance.nextTurnType),
                    nextTurnType       = guidance.nextTurnType,
                    distanceToNextTurn = guidance.distanceToNextTurn,
                )
                stateHolder.currentInstruction.value = instruction
                actionUploader.upload(instruction)

                when (guidance.action) {
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

        // 제자리에서 매 위치 업데이트마다 재탐색하는 폭주 방지:
        // 직전 재탐색 이후 20m 이상 이동했거나 10초가 지났을 때만 재요청한다.
        val now        = System.currentTimeMillis()
        val movedFar   = actionCalculator.haversine(lat, lng, lastRerouteLat, lastRerouteLng) > 20.0
        val cooledDown = now - lastRerouteAt > 10_000L
        if (!movedFar && !cooledDown) return

        isRerouting = true
        fetchRouteUseCase(lat, lng, config.destinationLat, config.destinationLng)
            .onSuccess { newSteps ->
                config.steps   = newSteps
                lastRerouteAt  = now
                lastRerouteLat = lat
                lastRerouteLng = lng
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

private fun NavigationAction.toKoreanMessage(distanceToNextTurn: Double?, nextTurnType: TurnType?): String = when (this) {
    NavigationAction.STRAIGHT      ->
        if (distanceToNextTurn != null && nextTurnType != null) {
            "${distanceToNextTurn.toInt()}m 후 ${nextTurnType.toKorean()}"
        } else {
            "직진"
        }
    NavigationAction.PREPARE_LEFT  -> "${distanceToNextTurn?.toInt() ?: 0}m 앞 좌회전"
    NavigationAction.LEFT          -> "좌회전"
    NavigationAction.PREPARE_RIGHT -> "${distanceToNextTurn?.toInt() ?: 0}m 앞 우회전"
    NavigationAction.RIGHT         -> "우회전"
    NavigationAction.ARRIVED       -> "목적지 도착"
    NavigationAction.REROUTE       -> "경로 재탐색 중"
}

private fun TurnType.toKorean(): String = when (this) {
    TurnType.LEFT  -> "좌회전"
    TurnType.RIGHT -> "우회전"
    TurnType.STRAIGHT -> "직진"
}
