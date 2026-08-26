package com.aicane.app.ui.screen.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aicane.app.presentation.device.DeviceViewModel
import com.aicane.app.ui.component.AiCaneTextField
import com.aicane.app.ui.component.FullWidthPillButton
import com.aicane.app.ui.component.StepIndicator
import com.aicane.app.ui.theme.*

@Composable
fun DeviceScreen(
    viewModel: DeviceViewModel = hiltViewModel(),
) {
    var deviceId by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Canvas)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(24.dp))

        StepIndicator(current = 1, total = 2)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 눈물방울 아이콘 (테두리만 있는 rounded box, -45도 회전)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .rotate(-45f)
                    .border(
                        width = 5.dp,
                        color = Ink,
                        shape = RoundedCornerShape(
                            topStart = 999.dp,
                            topEnd = 999.dp,
                            bottomEnd = 999.dp,
                            bottomStart = 2.dp,
                        ),
                    ),
            )

            Spacer(Modifier.height(28.dp))

            Text(
                text = "디바이스 등록",
                style = DisplayMd,
                color = Ink,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "AI-Cane 지팡이의 Device ID를 입력해주세요.",
                style = BodyMd,
                color = TextBody,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(28.dp))

            AiCaneTextField(
                value = deviceId,
                onValueChange = { deviceId = it; viewModel.clearError() },
                label = "",
                placeholder = "예: aicane-0001",
                isError = uiState.errorMessage.isNotEmpty(),
                errorMessage = uiState.errorMessage,
                caption = "지팡이 손잡이 안쪽 라벨에서 확인할 수 있습니다",
            )
        }

        FullWidthPillButton(
            text = "기기 등록",
            onClick = { viewModel.register(deviceId) },
            isLoading = uiState.isLoading,
            enabled = deviceId.isNotBlank() && !uiState.isLoading,
        )

        Spacer(Modifier.height(24.dp))
    }
}
