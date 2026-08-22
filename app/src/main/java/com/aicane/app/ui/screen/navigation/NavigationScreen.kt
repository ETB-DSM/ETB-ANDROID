package com.aicane.app.ui.screen.navigation

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.aicane.app.domain.model.NavigationAction
import com.aicane.app.presentation.navigation.NavigationViewModel
import com.aicane.app.ui.component.FullWidthPillButton
import com.aicane.app.ui.component.PillButtonVariant
import com.aicane.app.ui.theme.*

private val ErrorRed = Color(0xFFE53935)

@Composable
fun NavigationScreen(
    sessionId: String,
    destLat: Double,
    destLng: Double,
    destRadius: Double,
    destName: String = "목적지",
    viewModel: NavigationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) viewModel.startNavigation(sessionId, destLat, destLng, destRadius)
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.startNavigation(sessionId, destLat, destLng, destRadius)
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val instruction = uiState.instruction
    val action = instruction?.action ?: NavigationAction.STRAIGHT
    val actionLabel = action.toLabel()
    val remainingDistance = instruction?.distanceMeters?.let { "${it.toInt()}m" } ?: ""
    val background: Color = if (action == NavigationAction.REROUTE) InkElevated else Ink

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background),
    ) {
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = OnInk)
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(text = actionLabel, style = DisplayXL, color = OnInk)
                    Spacer(Modifier.height(8.dp))
                    if (remainingDistance.isNotEmpty()) {
                        Text(
                            text = remainingDistance,
                            style = BodyLg,
                            color = OnInk.copy(alpha = 0.6f),
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(OnInk.copy(alpha = 0.15f))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                    ) {
                        Text(text = "진동으로 알림", style = Caption, color = OnInk.copy(alpha = 0.8f))
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(Canvas)
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(text = "목적지: $destName", style = BodyMdStrong, color = Ink)
                            if (remainingDistance.isNotEmpty()) {
                                Spacer(Modifier.height(2.dp))
                                Text(text = "$remainingDistance 남음", style = BodySm, color = TextBody)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(CanvasSoft)
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                        ) {
                            Text(text = actionLabel, style = BodySmStrong, color = Ink)
                        }
                    }

                    if (uiState.errorMessage.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Text(text = uiState.errorMessage, style = BodySm, color = ErrorRed)
                    }

                    Spacer(Modifier.weight(1f))

                    FullWidthPillButton(
                        text = "안내 종료",
                        onClick = { viewModel.cancelNavigation(sessionId) },
                        variant = PillButtonVariant.Danger,
                    )
                }
            }
        }
    }
}

private fun NavigationAction.toLabel(): String = when (this) {
    NavigationAction.STRAIGHT      -> "직진"
    NavigationAction.PREPARE_LEFT  -> "좌회전 준비"
    NavigationAction.LEFT          -> "좌회전"
    NavigationAction.PREPARE_RIGHT -> "우회전 준비"
    NavigationAction.RIGHT         -> "우회전"
    NavigationAction.ARRIVED       -> "도착"
    NavigationAction.REROUTE       -> "경로 재탐색"
}
