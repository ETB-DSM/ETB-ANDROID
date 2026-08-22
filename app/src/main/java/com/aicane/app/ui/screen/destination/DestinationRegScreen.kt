package com.aicane.app.ui.screen.destination

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aicane.app.ui.component.AiCaneTextField
import com.aicane.app.ui.component.BackButton
import com.aicane.app.ui.component.FullWidthPillButton
import com.aicane.app.ui.theme.*

@Composable
fun DestinationRegScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    prefilledAddress: String = "",
) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf(prefilledAddress) }
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

        BackButton(onClick = onBack)

        Spacer(Modifier.height(24.dp))

        Text(text = "목적지 저장", style = DisplayMd, color = Ink)

        Spacer(Modifier.height(8.dp))

        Text(
            text = "자주 가는 목적지를 이름과 함께 저장해두세요.",
            style = BodyMd,
            color = TextBody,
        )

        Spacer(Modifier.height(32.dp))

        AiCaneTextField(
            value = name,
            onValueChange = { name = it; errorMsg = "" },
            label = "이름",
            placeholder = "예: 집, 회사, 병원",
            isError = errorMsg.isNotEmpty(),
            errorMessage = errorMsg,
        )

        Spacer(Modifier.height(16.dp))

        AiCaneTextField(
            value = address,
            onValueChange = { address = it },
            label = "주소",
            placeholder = "주소를 입력하세요",
        )

        Spacer(Modifier.weight(1f))

        FullWidthPillButton(
            text = "저장",
            onClick = { isLoading = true; onSaved() },
            isLoading = isLoading,
            enabled = name.isNotBlank() && address.isNotBlank(),
        )

        Spacer(Modifier.height(24.dp))
    }
}
