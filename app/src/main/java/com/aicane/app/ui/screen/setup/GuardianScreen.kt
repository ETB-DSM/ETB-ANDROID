package com.aicane.app.ui.screen.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aicane.app.presentation.guardian.GuardianViewModel
import com.aicane.app.ui.component.AiCaneTextField
import com.aicane.app.ui.component.BackButton
import com.aicane.app.ui.component.FullWidthPillButton
import com.aicane.app.ui.component.PillButtonVariant
import com.aicane.app.ui.component.StepIndicator
import com.aicane.app.ui.theme.*

@Composable
fun GuardianScreen(
    onSkip: () -> Unit = {},
    standalone: Boolean = false,
    onBack: () -> Unit = {},
    viewModel: GuardianViewModel = hiltViewModel(),
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

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

        if (standalone) BackButton(onClick = onBack) else StepIndicator(current = 2, total = 2)

        Spacer(Modifier.height(32.dp))

        Text(text = "보호자 등록", style = DisplayMd, color = Ink)

        Spacer(Modifier.height(8.dp))

        Text(
            text = "비상 상황 시 연락받을 보호자 정보를 등록해주세요.\n나중에 마이페이지에서 변경할 수 있습니다.",
            style = BodyMd,
            color = TextBody,
        )

        Spacer(Modifier.height(32.dp))

        AiCaneTextField(
            value = name,
            onValueChange = { name = it; viewModel.clearError() },
            label = "보호자 이름",
            placeholder = "이름을 입력하세요",
        )

        Spacer(Modifier.height(16.dp))

        AiCaneTextField(
            value = phone,
            onValueChange = { phone = it; viewModel.clearError() },
            label = "전화번호",
            placeholder = "010-0000-0000",
            keyboardType = KeyboardType.Phone,
            isError = uiState.errorMessage.isNotEmpty(),
            errorMessage = uiState.errorMessage,
        )

        Spacer(Modifier.weight(1f))

        FullWidthPillButton(
            text = "보호자 등록",
            onClick = { viewModel.register(name, phone) },
            isLoading = uiState.isLoading,
            enabled = name.isNotBlank() && phone.isNotBlank() && !uiState.isLoading,
        )

        if (!standalone) {
            Spacer(Modifier.height(12.dp))
            FullWidthPillButton(
                text = "나중에 등록하기",
                onClick = onSkip,
                variant = PillButtonVariant.Subtle,
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}
