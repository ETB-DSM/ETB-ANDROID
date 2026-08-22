package com.aicane.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AiCaneColorScheme = lightColorScheme(
    primary          = Ink,
    onPrimary        = OnInk,
    background       = Canvas,
    onBackground     = Ink,
    surface          = Canvas,
    onSurface        = Ink,
    surfaceVariant   = CanvasSoft,
    onSurfaceVariant = TextBody,
    error            = Error,
    onError          = OnInk,
)

@Composable
fun AiCaneTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AiCaneColorScheme,
        typography  = AiCaneTypography,
        shapes      = AiCaneShapes,
        content     = content,
    )
}
