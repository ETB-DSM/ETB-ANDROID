package com.aicane.app.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aicane.app.ui.theme.*

enum class PillButtonVariant { Primary, Secondary, Subtle, Danger }

@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: PillButtonVariant = PillButtonVariant.Primary,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    val shape = AiCaneShapes.large // 999dp pill

    when (variant) {
        PillButtonVariant.Primary -> Button(
            onClick = onClick,
            enabled = enabled && !isLoading,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Ink,
                contentColor = OnInk,
                disabledContainerColor = CanvasSoft,
                disabledContentColor = TextMute,
            ),
            modifier = modifier.height(56.dp),
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = OnInk, strokeWidth = 2.dp)
            else Text(text = text, style = ButtonLg)
        }

        PillButtonVariant.Secondary -> OutlinedButton(
            onClick = onClick,
            enabled = enabled && !isLoading,
            shape = shape,
            border = BorderStroke(1.dp, SurfacePressed),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Canvas,
                contentColor = Ink,
            ),
            modifier = modifier.height(56.dp),
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Ink, strokeWidth = 2.dp)
            else Text(text = text, style = ButtonLg)
        }

        PillButtonVariant.Subtle -> Button(
            onClick = onClick,
            enabled = enabled && !isLoading,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = CanvasSoft,
                contentColor = Ink,
            ),
            modifier = modifier.height(56.dp),
        ) {
            Text(text = text, style = ButtonLg)
        }

        PillButtonVariant.Danger -> OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            shape = shape,
            border = BorderStroke(1.dp, SurfacePressed),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Canvas,
                contentColor = Error,
            ),
            modifier = modifier.height(56.dp),
        ) {
            Text(text = text, style = ButtonLg, color = Error)
        }
    }
}

@Composable
fun FullWidthPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: PillButtonVariant = PillButtonVariant.Primary,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    PillButton(
        text = text,
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        variant = variant,
        enabled = enabled,
        isLoading = isLoading,
    )
}
