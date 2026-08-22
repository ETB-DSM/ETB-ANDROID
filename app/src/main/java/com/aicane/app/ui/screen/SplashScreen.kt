package com.aicane.app.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.aicane.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToList: () -> Unit,
    hasSession: Boolean = false,
) {
    var textVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(400L)
        textVisible = true
        delay(1100L)
        if (hasSession) onNavigateToList() else onNavigateToLogin()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ring")
    val ring1Scale by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1.4f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseOut), RepeatMode.Restart),
        label = "ring1",
    )
    val ring1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseOut), RepeatMode.Restart),
        label = "ring1a",
    )
    val ring2Scale by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1.4f,
        animationSpec = infiniteRepeatable(tween(2000, delayMillis = 600, easing = EaseOut), RepeatMode.Restart),
        label = "ring2",
    )
    val ring2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(2000, delayMillis = 600, easing = EaseOut), RepeatMode.Restart),
        label = "ring2a",
    )
    val ring3Scale by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1.4f,
        animationSpec = infiniteRepeatable(tween(2000, delayMillis = 1200, easing = EaseOut), RepeatMode.Restart),
        label = "ring3",
    )
    val ring3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(2000, delayMillis = 1200, easing = EaseOut), RepeatMode.Restart),
        label = "ring3a",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Ink),
    ) {
        // 상단 42% — 세 겹 ringOut 애니메이션
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.42f)
                .align(Alignment.TopCenter),
            contentAlignment = Alignment.Center,
        ) {
            listOf(
                ring1Scale to ring1Alpha,
                ring2Scale to ring2Alpha,
                ring3Scale to ring3Alpha,
            ).forEach { (scale, alpha) ->
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(OnInk.copy(alpha = alpha)),
                )
            }
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(OnInk),
            )
        }

        // 하단 62% — 텍스트 슬라이드 인
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.62f)
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AnimatedVisibility(
                    visible = textVisible,
                    enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 2 },
                ) {
                    Text(text = "AI-Cane", style = DisplayXL, color = OnInk)
                }
                Spacer(Modifier.height(8.dp))
                AnimatedVisibility(
                    visible = textVisible,
                    enter = fadeIn(tween(600, delayMillis = 100)) + slideInVertically(tween(600, delayMillis = 100)) { it / 2 },
                ) {
                    Text(text = "걸음마다, 함께 읽는 길", style = BodyLg, color = OnInk.copy(alpha = 0.7f))
                }
            }
        }
    }
}
