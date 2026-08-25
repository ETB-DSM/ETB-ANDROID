package com.aicane.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.aicane.app.ui.theme.*

@Composable
fun AiCaneTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    isError: Boolean = false,
    errorMessage: String = "",
    caption: String = "",
    enabled: Boolean = true,
    compact: Boolean = false,
) {
    val shape = RoundedCornerShape(8.dp)
    val borderColor = when {
        isError   -> Error
        !enabled  -> CanvasSoft
        else      -> CanvasSoft
    }
    val bgColor  = if (enabled) CanvasSoft else SurfacePressed
    val textColor = if (enabled) Ink else TextMute

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (label.isNotEmpty()) {
            Text(text = label, style = BodySm, color = TextBody)
        }
        BasicTextField(
            value = value,
            onValueChange = if (enabled) onValueChange else { _ -> },
            singleLine = true,
            readOnly = !enabled,
            textStyle = BodyLg.copy(color = textColor),
            cursorBrush = SolidColor(if (enabled) Ink else androidx.compose.ui.graphics.Color.Transparent),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (compact) 56.dp else 64.dp)
                        .clip(shape)
                        .background(bgColor)
                        .border(1.dp, borderColor, shape)
                        .padding(horizontal = 18.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (value.isEmpty()) {
                        Text(text = placeholder, style = BodyLg, color = TextMute)
                    }
                    innerTextField()
                }
            },
        )
        if (isError && errorMessage.isNotEmpty()) {
            Text(text = errorMessage, style = Caption, color = Error)
        } else if (caption.isNotEmpty()) {
            Text(text = caption, style = Caption, color = TextMute)
        }
    }
}
