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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aicane.app.ui.theme.*

data class DestinationItem(val id: String, val name: String, val address: String)

@Composable
fun DestinationListScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToMypage: () -> Unit,
    onStartNavigation: (sessionId: String) -> Unit,
) {
    val destinations = remember {
        mutableStateListOf(
            DestinationItem("1", "집", "서울특별시 강남구 테헤란로 123"),
            DestinationItem("2", "병원", "서울특별시 강남구 삼성로 456"),
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
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "목적지 추가",
                        tint = Ink,
                    )
                }
                IconButton(
                    onClick = onNavigateToMypage,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Ink),
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "마이페이지",
                        tint = OnInk,
                    )
                }
            }
        }

        if (destinations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "등록된 목적지가 없어요", style = DisplaySm, color = TextBody)
                    Spacer(Modifier.height(8.dp))
                    Text(text = "+ 버튼으로 목적지를 추가해보세요", style = BodyMd, color = TextMute)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(destinations) { dest ->
                    DestinationCard(
                        item = dest,
                        onStart = { onStartNavigation("session_${dest.id}") },
                    )
                }
            }
        }
    }
}

@Composable
private fun DestinationCard(
    item: DestinationItem,
    onStart: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CanvasSoft)
            .clickable { onStart() }
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name, style = BodyMdStrong, color = Ink)
            Spacer(Modifier.height(4.dp))
            Text(text = item.address, style = BodySm, color = TextBody)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(Ink)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(text = "출발", style = BodySmStrong, color = OnInk)
        }
    }
}
