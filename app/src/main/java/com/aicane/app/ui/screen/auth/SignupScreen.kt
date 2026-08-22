package com.aicane.app.ui.screen.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aicane.app.presentation.auth.SignupViewModel
import com.aicane.app.ui.component.AiCaneTextField
import com.aicane.app.ui.component.BackButton
import com.aicane.app.ui.component.FullWidthPillButton
import com.aicane.app.ui.component.StepIndicator
import com.aicane.app.ui.theme.*

private enum class SignupStep { Email, Name, Password }

@Composable
fun SignupScreen(
    onBack: () -> Unit,
    viewModel: SignupViewModel = hiltViewModel(),
) {
    var step by remember { mutableStateOf(SignupStep.Email) }
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val currentStep = step.ordinal + 1

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
            BackButton(
                onClick = {
                    when (step) {
                        SignupStep.Email    -> onBack()
                        SignupStep.Name     -> { step = SignupStep.Email; viewModel.clearError() }
                        SignupStep.Password -> { step = SignupStep.Name; viewModel.clearError() }
                    }
                },
            )
            StepIndicator(
                current = currentStep,
                total = 3,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
        }

        Spacer(Modifier.height(32.dp))

        AnimatedContent(
            targetState = step,
            transitionSpec = {
                (fadeIn() + slideInVertically { it / 4 }).togetherWith(fadeOut())
            },
            label = "signup_step",
        ) { currentStepState ->
            Column(modifier = Modifier.fillMaxWidth()) {
                when (currentStepState) {
                    SignupStep.Email -> {
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
                            onValueChange = { email = it; viewModel.clearError() },
                            label = "이메일",
                            placeholder = "example@email.com",
                            keyboardType = KeyboardType.Email,
                            isError = uiState.errorMessage.isNotEmpty(),
                            errorMessage = uiState.errorMessage,
                        )
                    }
                    SignupStep.Name -> {
                        Text(text = "이름", style = DisplayMd, color = Ink)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "서비스에서 사용할 이름을 입력해주세요.",
                            style = BodyMd,
                            color = TextBody,
                        )
                        Spacer(Modifier.height(32.dp))
                        AiCaneTextField(
                            value = name,
                            onValueChange = { name = it; viewModel.clearError() },
                            label = "이름",
                            placeholder = "이름을 입력하세요",
                            isError = uiState.errorMessage.isNotEmpty(),
                            errorMessage = uiState.errorMessage,
                        )
                    }
                    SignupStep.Password -> {
                        Text(text = "비밀번호", style = DisplayMd, color = Ink)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "8자 이상의 비밀번호를 설정해주세요.",
                            style = BodyMd,
                            color = TextBody,
                        )
                        Spacer(Modifier.height(32.dp))
                        AiCaneTextField(
                            value = password,
                            onValueChange = { password = it; viewModel.clearError() },
                            label = "비밀번호",
                            placeholder = "8자 이상 입력",
                            isPassword = true,
                            isError = uiState.errorMessage.isNotEmpty(),
                            errorMessage = uiState.errorMessage,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        FullWidthPillButton(
            text = when (step) {
                SignupStep.Email    -> "다음"
                SignupStep.Name     -> "다음"
                SignupStep.Password -> "회원가입"
            },
            onClick = {
                when (step) {
                    SignupStep.Email    -> step = SignupStep.Name
                    SignupStep.Name     -> step = SignupStep.Password
                    SignupStep.Password -> viewModel.signup(email, name, password)
                }
            },
            isLoading = uiState.isLoading,
            enabled = when (step) {
                SignupStep.Email    -> email.isNotBlank()
                SignupStep.Name     -> name.isNotBlank()
                SignupStep.Password -> password.length >= 8 && !uiState.isLoading
            },
        )

        Spacer(Modifier.height(24.dp))
    }
}
