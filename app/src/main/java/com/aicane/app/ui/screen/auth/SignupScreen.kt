package com.aicane.app.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aicane.app.ui.component.AiCaneTextField
import com.aicane.app.ui.component.BackButton
import com.aicane.app.ui.component.FullWidthPillButton
import com.aicane.app.ui.component.StepIndicator
import com.aicane.app.ui.theme.*

@Composable
fun SignupScreen(
    onBack: () -> Unit,
    onSignupSuccess: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

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
            StepIndicator(current = 1, total = 2, modifier = Modifier.align(androidx.compose.ui.Alignment.CenterVertically))
        }

        Spacer(Modifier.height(32.dp))

        Text(text = "회원가입", style = DisplayMd, color = Ink)

        Spacer(Modifier.height(8.dp))

        Text(
            text = "사용하실 이메일 주소를 입력해주세요.\n인증 코드가 해당 주소로 전송됩니다.",
            style = BodyMd,
            color = TextBody,
        )

        Spacer(Modifier.height(32.dp))

        AiCaneTextField(
            value = email,
            onValueChange = { email = it; errorMsg = "" },
            label = "이메일",
            placeholder = "example@email.com",
            keyboardType = KeyboardType.Email,
            isError = errorMsg.isNotEmpty(),
            errorMessage = errorMsg,
        )

        Spacer(Modifier.weight(1f))

        FullWidthPillButton(
            text = "인증 메일 보내기",
            onClick = { isLoading = true; onSignupSuccess() },
            isLoading = isLoading,
            enabled = email.isNotBlank(),
        )

        Spacer(Modifier.height(24.dp))
    }
}
