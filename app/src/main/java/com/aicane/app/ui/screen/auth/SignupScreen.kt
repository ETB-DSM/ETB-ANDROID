package com.aicane.app.ui.screen.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import com.aicane.app.BuildConfig
import com.aicane.app.presentation.auth.SignupViewModel
import com.aicane.app.ui.component.AiCaneTextField
import com.aicane.app.ui.component.BackButton
import com.aicane.app.ui.component.FullWidthPillButton
import com.aicane.app.ui.component.PillButtonVariant
import com.aicane.app.ui.component.StepIndicator
import com.aicane.app.ui.theme.*
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

private enum class SignupStep { Email, Name, Password }

@Composable
fun SignupScreen(
    onBack: () -> Unit,
    viewModel: SignupViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    var step by remember { mutableStateOf(SignupStep.Email) }
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val currentStep = step.ordinal + 1

    val launchGoogleSignIn: () -> Unit = {
        coroutineScope.launch {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                val result = credentialManager.getCredential(context, request)
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(result.credential.data)
                viewModel.loginWithGoogle(googleIdTokenCredential.idToken)
            } catch (e: GetCredentialException) {
                viewModel.setError("구글 로그인에 실패했습니다.")
            }
        }
    }

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
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Box(modifier = Modifier.weight(1f).height(1.dp).background(SurfacePressed))
                            Text(text = "또는", style = BodySm, color = TextMute)
                            Box(modifier = Modifier.weight(1f).height(1.dp).background(SurfacePressed))
                        }
                        Spacer(Modifier.height(16.dp))
                        FullWidthPillButton(
                            text = "Google로 가입하기",
                            onClick = launchGoogleSignIn,
                            variant = PillButtonVariant.Secondary,
                            enabled = !uiState.isLoading,
                            isLoading = uiState.isLoading,
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
                            caption = "실명을 입력해주세요. 보호자에게 표시됩니다.",
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
                        )
                        Spacer(Modifier.height(12.dp))
                        AiCaneTextField(
                            value = passwordConfirm,
                            onValueChange = { passwordConfirm = it; viewModel.clearError() },
                            label = "비밀번호 확인",
                            placeholder = "비밀번호를 다시 입력하세요",
                            isPassword = true,
                            isError = uiState.errorMessage.isNotEmpty() ||
                                    (passwordConfirm.isNotEmpty() && password != passwordConfirm),
                            errorMessage = when {
                                uiState.errorMessage.isNotEmpty() -> uiState.errorMessage
                                passwordConfirm.isNotEmpty() && password != passwordConfirm -> "비밀번호가 일치하지 않습니다."
                                else -> ""
                            },
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
                SignupStep.Password -> password.length >= 8 && password == passwordConfirm && !uiState.isLoading
            },
        )

        Spacer(Modifier.height(24.dp))
    }
}
