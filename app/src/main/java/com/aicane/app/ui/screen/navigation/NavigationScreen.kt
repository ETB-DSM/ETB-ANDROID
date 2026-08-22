package com.aicane.app.ui.screen.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aicane.app.ui.component.FullWidthPillButton
import com.aicane.app.ui.component.PillButtonVariant
import com.aicane.app.ui.theme.*

private enum class NavAction { Straight, PrepareLeft, Left, PrepareRight, Right, Reroute }

private val NavAction.background: Color
    get() = when (this) {
        NavAction.Reroute -> InkElevated
        else              -> Ink
    }

@Composable
fun NavigationScreen(
    onEnd: () -> Unit,
    sessionId: String = "",
    destination: String = "목적지",
) {
    var currentAction by remember { mutableStateOf(NavAction.Straight) }
    var actionLabel by remember { mutableStateOf("직진") }
    var remainingDistance by remember { mutableStateOf("320m") }

    val nextGuidances = remember {
        listOf("200m 앞 좌회전", "100m 앞 직진 유지", "목적지 도착")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(currentAction.background),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 상단 320dp 블랙 밴드
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = actionLabel,
                    style = DisplayXL,
                    color = OnInk,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = remainingDistance,
                    style = BodyLg,
                    color = OnInk.copy(alpha = 0.6f),
                )
                Spacer(Modifier.height(16.dp))
                // 진동 힌트 pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(OnInk.copy(alpha = 0.15f))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                ) {
                    Text(text = "진동으로 알림", style = Caption, color = OnInk.copy(alpha = 0.8f))
                }
            }

            // 하단 화이트 카드
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Canvas)
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
            ) {
                // 목적지 + 길안내 상태
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(text = "목적지: $destination", style = BodyMdStrong, color = Ink)
                        Spacer(Modifier.height(2.dp))
                        Text(text = remainingDistance + " 남음", style = BodySm, color = TextBody)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(CanvasSoft)
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                    ) {
                        Text(text = actionLabel, style = BodySmStrong, color = Ink)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // 다음 안내 목록
                Text(text = "다음 안내", style = BodySmStrong, color = TextBody)
                Spacer(Modifier.height(8.dp))
                nextGuidances.forEachIndexed { index, guidance ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(if (index == 0) Ink else SurfacePressed),
                        )
                        Text(
                            text = guidance,
                            style = BodySm,
                            color = if (index == 0) Ink else TextMute,
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                FullWidthPillButton(
                    text = "안내 종료",
                    onClick = onEnd,
                    variant = PillButtonVariant.Danger,
                )
            }
        }
    }
}
