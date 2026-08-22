package com.aicane.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.aicane.app.R

val Pretendard = FontFamily(
    Font(R.font.pretendard_regular,  FontWeight.Normal),
    Font(R.font.pretendard_medium,   FontWeight.Medium),
    Font(R.font.pretendard_semibold, FontWeight.SemiBold),
    Font(R.font.pretendard_bold,     FontWeight.Bold),
)

// HTML prototype → Compose TextStyle mapping (DESIGN.md tokens)
val DisplayXL   = TextStyle(fontFamily = Pretendard, fontWeight = FontWeight.Bold,   fontSize = 36.sp, lineHeight = 44.sp, letterSpacing = (-0.01).sp)
val DisplayLg   = TextStyle(fontFamily = Pretendard, fontWeight = FontWeight.Bold,   fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = (-0.01).sp)
val DisplayMd   = TextStyle(fontFamily = Pretendard, fontWeight = FontWeight.Bold,   fontSize = 24.sp, lineHeight = 32.sp)
val DisplaySm   = TextStyle(fontFamily = Pretendard, fontWeight = FontWeight.Bold,   fontSize = 20.sp, lineHeight = 28.sp)
val BodyLg      = TextStyle(fontFamily = Pretendard, fontWeight = FontWeight.Medium, fontSize = 18.sp, lineHeight = 24.sp)
val BodyMd      = TextStyle(fontFamily = Pretendard, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp)
val BodyMdStrong= TextStyle(fontFamily = Pretendard, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 20.sp)
val BodySm      = TextStyle(fontFamily = Pretendard, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp)
val BodySmStrong= TextStyle(fontFamily = Pretendard, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 16.sp)
val Caption     = TextStyle(fontFamily = Pretendard, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 20.sp)
val ButtonLg    = TextStyle(fontFamily = Pretendard, fontWeight = FontWeight.Medium, fontSize = 18.sp, lineHeight = 24.sp)
val ButtonMd    = TextStyle(fontFamily = Pretendard, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 20.sp)

val AiCaneTypography = Typography(
    displayLarge  = DisplayXL,
    displayMedium = DisplayLg,
    displaySmall  = DisplayMd,
    headlineLarge = DisplaySm,
    bodyLarge     = BodyLg,
    bodyMedium    = BodyMd,
    bodySmall     = BodySm,
    labelLarge    = ButtonMd,
    labelSmall    = Caption,
)
