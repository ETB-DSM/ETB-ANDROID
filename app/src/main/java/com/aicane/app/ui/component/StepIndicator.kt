package com.aicane.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aicane.app.ui.theme.*

@Composable
fun StepIndicator(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        repeat(total) { index ->
            val isActive = index == current - 1
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(if (isActive) 28.dp else 8.dp)
                    .background(
                        color = if (isActive) Ink else SurfacePressed,
                        shape = RoundedCornerShape(999.dp),
                    )
            )
        }
        Spacer(Modifier.width(6.dp))
        Text(
            text = "$current / $total 단계",
            style = Caption,
            color = TextMute,
        )
    }
}
