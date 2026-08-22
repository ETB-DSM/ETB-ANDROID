package com.aicane.app.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aicane.app.ui.theme.*

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = if (isDark) InkElevated else CanvasSoft,
            contentColor   = if (isDark) OnInk else Ink,
        ),
    ) {
        // Using text arrow as fallback; replace with actual vector icon
        androidx.compose.material3.Text(
            text = "←",
            style = BodyMd,
            color = if (isDark) OnInk else Ink,
        )
    }
}
