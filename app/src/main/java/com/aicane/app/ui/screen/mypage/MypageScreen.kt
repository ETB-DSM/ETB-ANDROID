package com.aicane.app.ui.screen.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aicane.app.presentation.mypage.MypageViewModel
import com.aicane.app.ui.component.BackButton
import com.aicane.app.ui.component.FullWidthPillButton
import com.aicane.app.ui.component.PillButtonVariant
import com.aicane.app.ui.screen.destination.DeleteConfirmDialog
import com.aicane.app.ui.theme.*

@Composable
fun MypageScreen(
    onBack: () -> Unit,
    viewModel: MypageViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showDeleteDeviceDialog) {
        DeleteConfirmDialog(
            title = "기기를 삭제할까요?",
            message = "삭제한 기기는 복구할 수 없어요.",
            onConfirm = { viewModel.deleteDevice() },
            onDismiss = { viewModel.cancelDeleteDevice() },
        )
    }

    if (uiState.showDeleteGuardianDialog) {
        DeleteConfirmDialog(
            title = "보호자를 삭제할까요?",
            message = "삭제한 보호자는 복구할 수 없어요.",
            onConfirm = { viewModel.deleteGuardian() },
            onDismiss = { viewModel.cancelDeleteGuardian() },
        )
    }

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
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 30.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                // 뒤로 버튼 + 타이틀
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BackButton(onClick = onBack, isDark = true)
                    Spacer(Modifier.width(14.dp))
                    Text(text = "마이페이지", style = BodyMdStrong, color = TextMute)
                }

                // 이니셜 원 + 이름/이메일
                val initial = uiState.userName.trim().firstOrNull()?.toString() ?: "?"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(InkElevated)
                            .border(3.dp, OnInk, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = initial, style = DisplaySm, color = OnInk)
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = uiState.userName.ifEmpty { "사용자" },
                            style = DisplayMd,
                            color = OnInk,
                            maxLines = 1,
                        )
                        Text(
                            text = uiState.userEmail,
                            style = BodySm,
                            color = TextMute,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                // Status chips
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val deviceChip = if (uiState.deviceId.isNotEmpty()) "지팡이 연결됨" else "지팡이 미연결"
                    val guardChip  = if (uiState.guardianName.isNotEmpty()) "보호자 ${uiState.guardianName}" else "보호자 미등록"
                    listOf(deviceChip, guardChip).forEach { label ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(InkElevated)
                                .padding(horizontal = 16.dp, vertical = 9.dp),
                        ) {
                            Text(text = label, style = BodySmStrong, color = OnInk)
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Ink)
                }
            } else {
                DeletableSectionCard(
                    title = "등록된 기기",
                    showDelete = uiState.deviceId.isNotEmpty(),
                    onDelete = { viewModel.confirmDeleteDevice() },
                ) {
                    InfoRow(label = "기기 ID", value = uiState.deviceId.ifEmpty { "-" })
                }

                DeletableSectionCard(
                    title = "보호자 정보",
                    showDelete = uiState.guardianId.isNotEmpty(),
                    onDelete = { viewModel.confirmDeleteGuardian() },
                ) {
                    InfoRow(label = "이름", value = uiState.guardianName.ifEmpty { "-" })
                    InfoRow(label = "전화번호", value = uiState.guardianPhone.ifEmpty { "-" })
                }

                if (uiState.errorMessage.isNotEmpty()) {
                    Text(text = uiState.errorMessage, style = BodySm, color = TextBody)
                }
            }

            FullWidthPillButton(
                text = "로그아웃",
                onClick = { viewModel.logout() },
                variant = PillButtonVariant.Danger,
            )
        }

        NavigationBarPadding()
    }
}

@Composable
private fun DeletableSectionCard(
    title: String,
    showDelete: Boolean,
    onDelete: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CanvasSoft)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = title, style = BodySmStrong, color = TextBody)
            if (showDelete) {
                Text(
                    text = "삭제",
                    style = BodySm,
                    color = Error,
                    modifier = Modifier.clickable { onDelete() },
                )
            }
        }
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
