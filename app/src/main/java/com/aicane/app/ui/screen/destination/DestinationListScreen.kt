package com.aicane.app.ui.screen.destination

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.aicane.app.domain.model.Destination
import com.aicane.app.presentation.destination.DestinationListViewModel
import com.aicane.app.ui.theme.*

@Composable
fun DestinationListScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToMypage: () -> Unit,
    viewModel: DestinationListViewModel = hiltViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.loadDestinations()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val uiState by viewModel.uiState.collectAsState()

    uiState.destinationToDelete?.let { target ->
        DeleteConfirmDialog(
            title = "목적지를 삭제할까요?",
            message = "삭제한 목적지는 복구할 수 없어요.",
            onConfirm = { viewModel.deleteDestination() },
            onDismiss = { viewModel.cancelDelete() },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Canvas)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "목적지", style = DisplaySm, color = Ink)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onNavigateToSearch,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(CanvasSoft),
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "목적지 추가", tint = Ink)
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Ink)
                        .clickable { onNavigateToMypage() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = uiState.userInitial,
                        style = BodyMdStrong,
                        color = OnInk,
                    )
                }
            }
        }

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Ink)
                }
            }
            uiState.destinations.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "등록된 목적지가 없어요", style = DisplaySm, color = TextBody)
                        Spacer(Modifier.height(8.dp))
                        Text(text = "+ 버튼으로 목적지를 추가해보세요", style = BodyMd, color = TextMute)
                    }
                }
            }
            else -> {
                if (uiState.errorMessage.isNotEmpty()) {
                    Text(
                        text = uiState.errorMessage,
                        style = BodySm,
                        color = Error,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    )
                }
                Text(
                    text = "카드를 눌러 길안내를 시작하세요",
                    style = Caption,
                    color = TextMute,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CanvasSofter),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    items(uiState.destinations, key = { it.destinationId }) { destination ->
                        DestinationCard(
                            destination = destination,
                            onStart = { viewModel.startNavigation(destination.destinationId) },
                            onDelete = { viewModel.confirmDelete(destination) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DestinationCard(
    destination: Destination,
    onStart: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Canvas)
            .clickable { onStart() }
            .padding(horizontal = 20.dp, vertical = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = destination.name, style = BodyMdStrong, color = Ink)
            Spacer(Modifier.height(6.dp))
            Text(
                text = "인식 텍스트 · ${destination.targetText} · ${destination.radius.toInt()}m",
                style = BodySm,
                color = TextBody,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "삭제",
                    tint = Error,
                    modifier = Modifier.size(20.dp),
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(CanvasSoft)
                    .height(44.dp)
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "길안내 시작", style = BodySmStrong, color = Ink)
            }
        }
    }
}

@Composable
internal fun DeleteConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Canvas,
        title = {
            Text(text = title, style = BodyMdStrong, color = Ink)
        },
        text = {
            Text(text = message, style = BodySm, color = TextBody)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "삭제", style = BodySmStrong, color = Error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "취소", style = BodySm, color = TextBody)
            }
        },
    )
}
