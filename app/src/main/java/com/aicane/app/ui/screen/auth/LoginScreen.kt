package com.aicane.app.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import com.aicane.app.BuildConfig
import com.aicane.app.presentation.auth.LoginViewModel
import com.aicane.app.ui.component.AiCaneTextField
import com.aicane.app.ui.component.FullWidthPillButton
import com.aicane.app.ui.component.PillButtonVariant
import com.aicane.app.ui.theme.*
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigateToSignup: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()

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
        verticalArrangement = Arrangement.Center,
    ) {
        // 링 아이콘 + 타이틀
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .border(5.dp, Ink, CircleShape),
            )
            Spacer(Modifier.height(12.dp))
            Text(text = "AI-Cane", style = DisplayLg, color = Ink)
        }

        Spacer(Modifier.height(32.dp))

        // 입력 카드
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CanvasSoft)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            AiCaneTextField(
                value = email,
                onValueChange = { email = it; viewModel.clearError() },
                label = "이메일",
                placeholder = "name@example.com",
                keyboardType = KeyboardType.Email,
                isError = uiState.errorMessage.isNotEmpty(),
                compact = true,
            )
            AiCaneTextField(
                value = password,
                onValueChange = { password = it; viewModel.clearError() },
                label = "비밀번호",
                placeholder = "비밀번호",
                isPassword = true,
                isError = uiState.errorMessage.isNotEmpty(),
                errorMessage = uiState.errorMessage,
                compact = true,
            )
        }

        Spacer(Modifier.height(16.dp))

        FullWidthPillButton(
            text = "로그인",
            onClick = { viewModel.login(email, password) },
            isLoading = uiState.isLoading,
            enabled = email.isNotBlank() && password.isNotBlank() && !uiState.isLoading,
        )

        Spacer(Modifier.height(16.dp))

        // 구분선
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
            text = "Google로 로그인",
            onClick = launchGoogleSignIn,
            variant = PillButtonVariant.Secondary,
            isLoading = uiState.isLoading,
            enabled = !uiState.isLoading,
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "계정이 없으신가요? 회원가입",
            style = BodyMd,
            color = LinkBlue,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { onNavigateToSignup() },
        )
    }
}
