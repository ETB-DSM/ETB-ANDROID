package com.aicane.app.ui.screen.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.aicane.app.ui.component.FullWidthPillButton
import com.aicane.app.ui.component.PillButtonVariant
import com.aicane.app.ui.theme.*

@Composable
fun NavigationScreen(
    onEnd: () -> Unit,
    sessionId: String = "",
    destination: String = "목적지",
) {
    var currentAction by remember { mutableStateOf("직진") }
    var remainingDistance by remember { mutableStateOf("320m") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Ink),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            Spacer(Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Canvas)
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column {
                        Text(text = remainingDistance, style = DisplayXL, color = Ink)
                        Spacer(Modifier.height(4.dp))
                        Text(text = "목적지: $destination", style = BodyMd, color = TextBody)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(CanvasSoft)
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Text(text = currentAction, style = BodyMdStrong, color = Ink)
                    }
                }

                Spacer(Modifier.height(24.dp))

                FullWidthPillButton(
                    text = "안내 종료",
                    onClick = onEnd,
                    variant = PillButtonVariant.Danger,
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = currentAction,
                style = DisplayXL,
                color = OnInk,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = remainingDistance,
                style = DisplayLg,
                color = OnInk.copy(alpha = 0.7f),
            )
        }
    }
}
