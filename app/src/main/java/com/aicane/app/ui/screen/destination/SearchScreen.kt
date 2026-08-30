package com.aicane.app.ui.screen.destination

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.aicane.app.presentation.destination.SearchViewModel
import com.aicane.app.ui.component.BackButton
import com.aicane.app.ui.theme.*
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

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

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLocatingCurrent by remember { mutableStateOf(false) }

    @SuppressLint("MissingPermission")
    fun useCurrentLocation() {
        coroutineScope.launch {
            isLocatingCurrent = true
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            val location = suspendCancellableCoroutine { cont ->
                fusedClient.lastLocation
                    .addOnSuccessListener { loc -> cont.resume(loc) }
                    .addOnFailureListener { cont.resume(null) }
            }
            isLocatingCurrent = false
            if (location != null) {
                onPlaceSelected(PlaceResult("현재 위치", "현재 위치", location.latitude, location.longitude))
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> if (isGranted) useCurrentLocation() }

    val onUseCurrentLocationClick: () -> Unit = {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            useCurrentLocation()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Canvas)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 24.dp, top = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            BackButton(onClick = onBack)
            Text(text = "목적지 검색", style = DisplaySm, color = Ink)
        }

        Spacer(Modifier.height(16.dp))

        // 검색 바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(64.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(CanvasSoft)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // 아웃라인 원형 아이콘
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .border(2.dp, TextBody, CircleShape),
            )

            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(text = "장소 이름을 입력하세요", style = BodyLg, color = TextMute)
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
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Canvas)
                        .clickable { query = ""; viewModel.onQueryChange("") },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "✕", style = BodySm, color = TextBody)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        when {
            query.isBlank() -> {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(CanvasSoft)
                            .clickable(enabled = !isLocatingCurrent) { onUseCurrentLocationClick() }
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .border(2.dp, TextBody, CircleShape),
                        )
                        Text(
                            text = if (isLocatingCurrent) "현재 위치를 확인하는 중..." else "현재 위치를 목적지로",
                            style = BodyMdStrong,
                            color = Ink,
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "장소명이나 주소를 입력하세요",
                            style = BodyMd,
                            color = TextMute,
                        )
                    }
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
            uiState.errorMessage.isNotEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = uiState.errorMessage, style = BodyMd, color = TextMute)
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
