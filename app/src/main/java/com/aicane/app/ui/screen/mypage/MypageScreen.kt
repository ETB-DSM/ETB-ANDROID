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
import com.aicane.app.domain.model.Device
import com.aicane.app.domain.model.Guardian
import com.aicane.app.presentation.mypage.MypageViewModel
import com.aicane.app.ui.component.BackButton
import com.aicane.app.ui.component.FullWidthPillButton
import com.aicane.app.ui.component.PillButtonVariant
import com.aicane.app.ui.screen.destination.DeleteConfirmDialog
import com.aicane.app.ui.theme.*

@Composable
fun MypageScreen(
    onBack: () -> Unit,
    onNavigateToAddDevice: () -> Unit,
    onNavigateToAddGuardian: () -> Unit,
    viewModel: MypageViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    uiState.deviceToDelete?.let { device ->
        DeleteConfirmDialog(
            title = "\"${device.name}\" 기기를 삭제할까요?",
            message = "삭제한 기기는 복구할 수 없어요.",
            onConfirm = { viewModel.deleteDevice() },
            onDismiss = { viewModel.cancelDeleteDevice() },
        )
    }

    uiState.guardianToDelete?.let { guardian ->
        DeleteConfirmDialog(
            title = "\"${guardian.name}\" 보호자를 삭제할까요?",
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BackButton(onClick = onBack, isDark = true)
                    Spacer(Modifier.width(14.dp))
                    Text(text = "마이페이지", style = BodyMdStrong, color = TextMute)
                }

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

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val deviceChip = if (uiState.devices.isNotEmpty()) "지팡이 ${uiState.devices.size}대 연결됨" else "지팡이 미연결"
                    val guardChip  = if (uiState.guardians.isNotEmpty()) "보호자 ${uiState.guardians.size}명 등록됨" else "보호자 미등록"
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
                ListSectionCard(
                    title = "등록된 지팡이",
                    isEmpty = uiState.devices.isEmpty(),
                    emptyText = "등록된 지팡이가 없어요",
                    canAdd = uiState.canAddDevice,
                    onAdd = onNavigateToAddDevice,
                ) {
                    uiState.devices.forEach { device ->
                        DeviceRow(device = device, onDelete = { viewModel.confirmDeleteDevice(device) })
                    }
                }

                ListSectionCard(
                    title = "보호자",
                    isEmpty = uiState.guardians.isEmpty(),
                    emptyText = "등록된 보호자가 없어요",
                    canAdd = uiState.canAddGuardian,
                    onAdd = onNavigateToAddGuardian,
                ) {
                    uiState.guardians.forEach { guardian ->
                        GuardianRow(guardian = guardian, onDelete = { viewModel.confirmDeleteGuardian(guardian) })
                    }
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
private fun ListSectionCard(
    title: String,
    isEmpty: Boolean,
    emptyText: String,
    canAdd: Boolean,
    onAdd: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CanvasSoft)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = title, style = BodySmStrong, color = TextBody)
            if (canAdd) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Canvas)
                        .border(1.dp, Ink, RoundedCornerShape(999.dp))
                        .clickable { onAdd() }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                ) {
                    Text(text = "+ 추가", style = BodySmStrong, color = Ink)
                }
            }
        }
        if (isEmpty) {
            Text(text = emptyText, style = BodySm, color = TextMute)
        } else {
            content()
        }
    }
}

@Composable
private fun DeviceRow(device: Device, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(text = device.name, style = BodyMdStrong, color = Ink)
            Spacer(Modifier.height(2.dp))
            Text(text = device.deviceId, style = Caption, color = TextMute)
        }
        Text(
            text = "해제",
            style = BodySm,
            color = Error,
            modifier = Modifier.clickable { onDelete() },
        )
    }
}

@Composable
private fun GuardianRow(guardian: Guardian, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(text = guardian.name, style = BodyMdStrong, color = Ink)
            Spacer(Modifier.height(2.dp))
            Text(text = guardian.phone, style = Caption, color = TextMute)
        }
        Text(
            text = "삭제",
            style = BodySm,
            color = Error,
            modifier = Modifier.clickable { onDelete() },
        )
    }
}

@Composable
private fun NavigationBarPadding() {
    Spacer(Modifier.navigationBarsPadding())
}
