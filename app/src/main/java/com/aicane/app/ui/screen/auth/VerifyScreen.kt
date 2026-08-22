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
fun VerifyScreen(
    onBack: () -> Unit,
    onVerifySuccess: () -> Unit,
) {
    var code by remember { mutableStateOf("") }
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
            StepIndicator(current = 2, total = 2, modifier = Modifier.align(androidx.compose.ui.Alignment.CenterVertically))
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
            onValueChange = { if (it.length <= 6) { code = it; errorMsg = "" } },
            label = "인증 코드",
            placeholder = "6자리 코드 입력",
            keyboardType = KeyboardType.Number,
            isError = errorMsg.isNotEmpty(),
            errorMessage = errorMsg,
        )

        Spacer(Modifier.weight(1f))

        FullWidthPillButton(
            text = "확인",
            onClick = { isLoading = true; onVerifySuccess() },
            isLoading = isLoading,
            enabled = code.length == 6,
        )

        Spacer(Modifier.height(24.dp))
    }
}
