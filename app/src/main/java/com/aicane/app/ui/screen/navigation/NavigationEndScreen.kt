package com.aicane.app.ui.screen.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aicane.app.domain.model.NavigationEndOutcome
import com.aicane.app.ui.component.FullWidthPillButton
import com.aicane.app.ui.theme.*

@Composable
fun NavigationEndScreen(
    onHome: () -> Unit,
    outcome: NavigationEndOutcome = NavigationEndOutcome.ARRIVED,
    destination: String = "목적지",
) {
    val headline = when (outcome) {
        NavigationEndOutcome.ARRIVED  -> "도착했어요!"
        NavigationEndOutcome.CANCELED -> "길안내를 종료했어요"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Canvas)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // 체크마크 원
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Ink),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "✓", style = DisplayMd, color = OnInk)
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = headline,
            style = DisplayMd,
            color = Ink,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = destination,
            style = BodyLg,
            color = TextBody,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(48.dp))

        FullWidthPillButton(
            text = "홈으로",
            onClick = onHome,
        )
    }
}
