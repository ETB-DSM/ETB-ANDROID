package com.aicane.app.ui.screen.destination

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aicane.app.presentation.destination.DestinationRegViewModel
import com.aicane.app.ui.component.AiCaneTextField
import com.aicane.app.ui.component.BackButton
import com.aicane.app.ui.component.FullWidthPillButton
import com.aicane.app.ui.theme.*

@Composable
fun DestinationRegScreen(
    onBack: () -> Unit,
    prefilledName: String = "",
    prefilledAddress: String = "",
    prefilledLat: Double = 0.0,
    prefilledLng: Double = 0.0,
    viewModel: DestinationRegViewModel = hiltViewModel(),
) {
    var name by remember { mutableStateOf(prefilledName) }
    var targetText by remember { mutableStateOf("") }
    var radius by remember { mutableStateOf("30") }

    val uiState by viewModel.uiState.collectAsState()

    val coordinateText = if (prefilledLat != 0.0 && prefilledLng != 0.0) {
        "%.6f, %.6f".format(prefilledLat, prefilledLng)
    } else {
        "좌표 정보 없음"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Canvas)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(16.dp))

            BackButton(onClick = onBack)

            Spacer(Modifier.height(24.dp))

            Text(text = "목적지 저장", style = DisplayMd, color = Ink)

            Spacer(Modifier.height(8.dp))

            Text(
                text = "자주 가는 목적지를 이름과 함께 저장해두세요.",
                style = BodyMd,
                color = TextBody,
            )

            Spacer(Modifier.height(32.dp))

            AiCaneTextField(
                value = name,
                onValueChange = { name = it; viewModel.clearError() },
                label = "이름",
                placeholder = "예: 집, 회사, 병원",
                isError = uiState.errorMessage.isNotEmpty(),
                errorMessage = uiState.errorMessage,
            )

            Spacer(Modifier.height(16.dp))

            AiCaneTextField(
                value = prefilledAddress,
                onValueChange = {},
                label = "주소",
                placeholder = "",
                enabled = false,
            )

            Spacer(Modifier.height(16.dp))

            AiCaneTextField(
                value = coordinateText,
                onValueChange = {},
                label = "좌표 (위도, 경도)",
                placeholder = "",
                enabled = false,
            )

            Spacer(Modifier.height(16.dp))

            AiCaneTextField(
                value = targetText,
                onValueChange = { targetText = it },
                label = "건물 입구 안내 문자",
                placeholder = "예: 강남역 2번 출구",
                caption = "Orange Pi가 목적지 도착을 판단할 때 사용하는 문자입니다.",
            )

            Spacer(Modifier.height(16.dp))

            AiCaneTextField(
                value = radius,
                onValueChange = { radius = it },
                label = "도착 반경 (m)",
                placeholder = "30",
                keyboardType = KeyboardType.Number,
                caption = "이 거리 이내 진입 시 도착으로 간주합니다.",
            )

            Spacer(Modifier.height(32.dp))
        }

        FullWidthPillButton(
            text = "저장",
            onClick = {
                viewModel.save(
                    name = name,
                    targetText = targetText,
                    latitude = prefilledLat,
                    longitude = prefilledLng,
                    radius = radius.toDoubleOrNull() ?: 30.0,
                )
            },
            isLoading = uiState.isLoading,
            enabled = name.isNotBlank()
                && prefilledAddress.isNotBlank()
                && targetText.isNotBlank()
                && radius.toDoubleOrNull() != null
                && !uiState.isLoading,
        )

        Spacer(Modifier.height(24.dp))
    }
}
