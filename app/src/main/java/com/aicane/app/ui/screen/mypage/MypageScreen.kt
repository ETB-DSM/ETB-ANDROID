package com.aicane.app.ui.screen.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.aicane.app.ui.component.BackButton
import com.aicane.app.ui.component.FullWidthPillButton
import com.aicane.app.ui.component.PillButtonVariant
import com.aicane.app.ui.theme.*

@Composable
fun MypageScreen(
    onBack: () -> Unit,
    userName: String = "홍길동",
    userEmail: String = "user@example.com",
    deviceId: String = "AICANE-001",
    guardianName: String = "보호자",
    guardianPhone: String = "010-0000-0000",
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Canvas)
            .verticalScroll(rememberScrollState()),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Ink)
                .statusBarsPadding()
                .padding(bottom = 32.dp),
        ) {
            BackButton(
                onClick = onBack,
                isDark = true,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(InkElevated),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = OnInk,
                        modifier = Modifier.size(32.dp),
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(text = userName, style = DisplaySm, color = OnInk)
                Spacer(Modifier.height(4.dp))
                Text(text = userEmail, style = BodyMd, color = OnInk.copy(alpha = 0.7f))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            SectionCard(title = "등록된 기기") {
                InfoRow(label = "기기 ID", value = deviceId)
            }

            SectionCard(title = "보호자 정보") {
                InfoRow(label = "이름", value = guardianName)
                InfoRow(label = "전화번호", value = guardianPhone)
            }

            FullWidthPillButton(
                text = "로그아웃",
                onClick = {},
                variant = PillButtonVariant.Danger,
            )
        }

        NavigationBarPadding()
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CanvasSoft)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = title, style = BodySmStrong, color = TextBody)
        content()
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = BodyMd, color = TextBody)
        Text(text = value, style = BodyMdStrong, color = Ink)
    }
}

@Composable
private fun NavigationBarPadding() {
    Spacer(Modifier.navigationBarsPadding())
}
