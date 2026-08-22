package com.aicane.app.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aicane.app.presentation.auth.VerifyViewModel
import com.aicane.app.ui.component.AiCaneTextField
import com.aicane.app.ui.component.BackButton
import com.aicane.app.ui.component.FullWidthPillButton
import com.aicane.app.ui.component.StepIndicator
import com.aicane.app.ui.theme.*

@Composable
fun VerifyScreen(
    email: String,
    onBack: () -> Unit,
    viewModel: VerifyViewModel = hiltViewModel(),
) {
    var code by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Canvas)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            BackButton(onClick = onBack)
            StepIndicator(
                current = 2,
                total = 2,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
        }

        Spacer(Modifier.height(32.dp))

        Text(text = "이메일 인증", style = DisplayMd, color = Ink)

        Spacer(Modifier.height(8.dp))

        Text(
            text = "이메일로 전송된 6자리 인증 코드를 입력해주세요.",
            style = BodyMd,
            color = TextBody,
        )

        Spacer(Modifier.height(32.dp))

        AiCaneTextField(
            value = code,
            onValueChange = { if (it.length <= 6) { code = it; viewModel.clearError() } },
            label = "인증 코드",
            placeholder = "6자리 코드 입력",
            keyboardType = KeyboardType.Number,
            isError = uiState.errorMessage.isNotEmpty(),
            errorMessage = uiState.errorMessage,
        )

        Spacer(Modifier.weight(1f))

        FullWidthPillButton(
            text = "확인",
            onClick = { viewModel.verifyEmail(email, code) },
            isLoading = uiState.isLoading,
            enabled = code.length == 6 && !uiState.isLoading,
        )

        Spacer(Modifier.height(24.dp))
    }
}
