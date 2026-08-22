package com.aicane.app.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aicane.app.presentation.auth.LoginViewModel
import com.aicane.app.ui.component.AiCaneTextField
import com.aicane.app.ui.component.FullWidthPillButton
import com.aicane.app.ui.component.PillButtonVariant
import com.aicane.app.ui.theme.*

@Composable
fun LoginScreen(
    onNavigateToSignup: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Canvas)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "AI Cane",
            style = DisplayXL,
            color = Ink,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "시각 장애인을 위한 스마트 지팡이",
            style = BodyMd,
            color = TextMute,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(48.dp))

        AiCaneTextField(
            value = email,
            onValueChange = { email = it; viewModel.clearError() },
            label = "이메일",
            placeholder = "example@email.com",
            keyboardType = KeyboardType.Email,
            isError = uiState.errorMessage.isNotEmpty(),
        )

        Spacer(Modifier.height(16.dp))

        AiCaneTextField(
            value = password,
            onValueChange = { password = it; viewModel.clearError() },
            label = "비밀번호",
            placeholder = "비밀번호를 입력하세요",
            isPassword = true,
            isError = uiState.errorMessage.isNotEmpty(),
            errorMessage = uiState.errorMessage,
        )

        Spacer(Modifier.height(32.dp))

        FullWidthPillButton(
            text = "로그인",
            onClick = { viewModel.login(email, password) },
            isLoading = uiState.isLoading,
            enabled = email.isNotBlank() && password.isNotBlank() && !uiState.isLoading,
        )

        Spacer(Modifier.height(16.dp))

        FullWidthPillButton(
            text = "Google로 로그인",
            onClick = { /* Google Sign-In SDK 연동 예정 */ },
            variant = PillButtonVariant.Secondary,
        )

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(text = "계정이 없으신가요?", style = BodySm, color = TextBody)
            Text(
                text = "회원가입",
                style = BodySmStrong,
                color = Ink,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { onNavigateToSignup() },
            )
        }
    }
}
