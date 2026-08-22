package com.aicane.app.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AiCaneShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),   // input fields
    small      = RoundedCornerShape(12.dp),
    medium     = RoundedCornerShape(16.dp),  // cards
    large      = RoundedCornerShape(999.dp), // pill buttons
    extraLarge = CircleShape,                // circular icon buttons
)
