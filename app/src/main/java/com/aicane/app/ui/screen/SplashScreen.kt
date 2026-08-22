package com.aicane.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.aicane.app.ui.theme.DisplayXL
import com.aicane.app.ui.theme.Ink
import com.aicane.app.ui.theme.OnInk
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToList: () -> Unit,
    hasSession: Boolean = false,
) {
    LaunchedEffect(Unit) {
        delay(1500L)
        if (hasSession) onNavigateToList() else onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Ink),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "AI Cane",
            style = DisplayXL,
            color = OnInk,
        )
    }
}
