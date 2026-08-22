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
import com.aicane.app.ui.component.AiCaneTextField
import com.aicane.app.ui.component.FullWidthPillButton
import com.aicane.app.ui.component.PillButtonVariant
import com.aicane.app.ui.theme.*

@Composable
fun LoginScreen(
    onNavigateToSignup: () -> Unit,
    onLoginSuccess: (isFirst: Boolean) -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

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
            onValueChange = { email = it; errorMsg = "" },
            label = "이메일",
            placeholder = "example@email.com",
            keyboardType = KeyboardType.Email,
            isError = errorMsg.isNotEmpty(),
        )

        Spacer(Modifier.height(16.dp))

        AiCaneTextField(
            value = password,
            onValueChange = { password = it; errorMsg = "" },
            label = "비밀번호",
            placeholder = "비밀번호를 입력하세요",
            isPassword = true,
            isError = errorMsg.isNotEmpty(),
            errorMessage = errorMsg,
        )

        Spacer(Modifier.height(32.dp))

        FullWidthPillButton(
            text = "로그인",
            onClick = { isLoading = true; onLoginSuccess(false) },
            isLoading = isLoading,
            enabled = email.isNotBlank() && password.isNotBlank(),
        )

        Spacer(Modifier.height(16.dp))

        FullWidthPillButton(
            text = "Google로 로그인",
            onClick = { onLoginSuccess(false) },
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
