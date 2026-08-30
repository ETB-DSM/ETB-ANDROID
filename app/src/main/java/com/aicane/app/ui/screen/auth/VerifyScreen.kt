package com.aicane.app.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aicane.app.presentation.auth.VerifyViewModel
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

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "이메일 인증",
                style = DisplayMd,
                color = Ink,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(12.dp))

            // Email pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(CanvasSoft)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(text = email, style = BodySm, color = TextBody)
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "위 주소로 전송된 6자리 인증 코드를 입력해주세요.",
                style = BodyMd,
                color = TextBody,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(32.dp))

            // Hidden input capturing keystrokes
            BasicTextField(
                value = code,
                onValueChange = {
                    if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                        code = it
                        viewModel.clearError()
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                cursorBrush = SolidColor(androidx.compose.ui.graphics.Color.Transparent),
                decorationBox = {
                    // 6-box OTP grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        repeat(6) { index ->
                            val char = code.getOrNull(index)
                            val isActive = index == code.length
                            val hasError = uiState.errorMessage.isNotEmpty()
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CanvasSoft)
                                    .border(
                                        width = if (isActive) 2.dp else 1.dp,
                                        color = when {
                                            hasError -> Error
                                            isActive -> Ink
                                            else     -> SurfacePressed
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = char?.toString() ?: "",
                                    style = DisplayMd,
                                    color = Ink,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                },
            )

            if (uiState.errorMessage.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = uiState.errorMessage,
                    style = Caption,
                    color = Error,
                    textAlign = TextAlign.Center,
                )
            } else if (uiState.infoMessage.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = uiState.infoMessage,
                    style = Caption,
                    color = TextBody,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.height(20.dp))

            val resendLabel = when {
                uiState.isResending          -> "재전송하는 중..."
                uiState.resendCooldownSec > 0 -> "코드 재전송 (${uiState.resendCooldownSec}초 후 가능)"
                else                          -> "코드 재전송"
            }
            Text(
                text = resendLabel,
                style = BodySm,
                color = if (uiState.isResending || uiState.resendCooldownSec > 0) TextMute else LinkBlue,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable(
                    enabled = !uiState.isResending && uiState.resendCooldownSec == 0,
                ) { viewModel.resendCode(email) },
            )
        }

        FullWidthPillButton(
            text = "확인",
            onClick = { viewModel.verifyEmail(email, code) },
            isLoading = uiState.isLoading,
            enabled = code.length == 6 && !uiState.isLoading,
        )

        Spacer(Modifier.height(24.dp))
    }
}
