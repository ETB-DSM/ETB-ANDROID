package com.aicane.app.ui.screen.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

        Spacer(Modifier.height(32.dp))

        Text(text = "기기 등록", style = DisplayMd, color = Ink)

        Spacer(Modifier.height(8.dp))

        Text(
            text = "지팡이 기기에 부착된 ID를 입력해주세요.\n기기 ID는 기기 하단에서 확인할 수 있습니다.",
            style = BodyMd,
            color = TextBody,
        )

        Spacer(Modifier.height(32.dp))

        AiCaneTextField(
            value = deviceId,
            onValueChange = { deviceId = it; viewModel.clearError() },
            label = "기기 ID",
            placeholder = "기기 ID를 입력하세요",
            isError = uiState.errorMessage.isNotEmpty(),
            errorMessage = uiState.errorMessage,
        )

        Spacer(Modifier.weight(1f))

        FullWidthPillButton(
            text = "기기 등록",
            onClick = { viewModel.register(deviceId) },
            isLoading = uiState.isLoading,
            enabled = deviceId.isNotBlank() && !uiState.isLoading,
        )

        Spacer(Modifier.height(24.dp))
    }
}
