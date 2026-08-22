package com.aicane.app.ui.screen.destination

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aicane.app.presentation.destination.SearchViewModel
import com.aicane.app.ui.theme.*

data class PlaceResult(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
)

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onPlaceSelected: (PlaceResult) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    var query by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(CanvasSoft)
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Ink),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = OnInk,
                        modifier = Modifier.size(16.dp),
                    )
                }

                Spacer(Modifier.width(12.dp))

                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(text = "목적지를 검색하세요", style = BodyLg, color = TextMute)
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = {
                            query = it
                            viewModel.onQueryChange(it)
                        },
                        singleLine = true,
                        textStyle = BodyLg.copy(color = Ink),
                        cursorBrush = SolidColor(Ink),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                    )
                }

                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            query = ""
                            viewModel.onQueryChange("")
                        },
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "검색어 지우기",
                            tint = TextBody,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }

            Text(
                text = "취소",
                style = BodyMdStrong,
                color = Ink,
                modifier = Modifier.clickable { onBack() },
            )
        }

        when {
            query.isBlank() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "장소명이나 주소를 입력하세요",
                        style = BodyMd,
                        color = TextMute,
                    )
                }
            }
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "검색 중...", style = BodyMd, color = TextMute)
                }
            }
            uiState.results.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "검색 결과가 없어요", style = BodyMdStrong, color = TextBody)
                        Spacer(Modifier.height(8.dp))
                        Text(text = "다른 검색어를 입력해보세요", style = BodySm, color = TextMute)
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                ) {
                    items(uiState.results) { place ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPlaceSelected(place) }
                                .padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(text = place.name, style = BodyMdStrong, color = Ink)
                                Spacer(Modifier.height(4.dp))
                                Text(text = place.address, style = BodySm, color = TextBody)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(CanvasSoft),
                        )
                    }
                }
            }
        }
    }
}
