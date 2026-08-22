package com.aicane.app.ui.screen.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aicane.app.ui.component.FullWidthPillButton
import com.aicane.app.ui.theme.*

@Composable
fun NavigationEndScreen(
    onHome: () -> Unit,
    destination: String = "목적지",
    walkingTime: String = "12분",
    walkingDistance: String = "980m",
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Ink)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "도착했어요!", style = DisplayXL, color = OnInk)

        Spacer(Modifier.height(12.dp))

        Text(text = destination, style = DisplayMd, color = OnInk.copy(alpha = 0.8f))

        Spacer(Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            StatItem(label = "이동 시간", value = walkingTime)
            StatItem(label = "이동 거리", value = walkingDistance)
        }

        Spacer(Modifier.height(64.dp))

        FullWidthPillButton(
            text = "홈으로",
            onClick = onHome,
            variant = com.aicane.app.ui.component.PillButtonVariant.Secondary,
        )
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = DisplayMd, color = OnInk)
        Spacer(Modifier.height(4.dp))
        Text(text = label, style = BodySm, color = OnInk.copy(alpha = 0.6f))
    }
}
