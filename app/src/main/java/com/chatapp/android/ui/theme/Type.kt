package com.chatapp.android.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Using system default sans-serif (Roboto on Android)
// Replace with Google Fonts if desired: com.google.android.material:material:x.x.x

val ChatTypography = Typography(
    headlineLarge = TextStyle(
        fontSize   = 28.sp,
        fontWeight = FontWeight.Bold,
        color      = TextPrimary,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontSize   = 22.sp,
        fontWeight = FontWeight.SemiBold,
        color      = TextPrimary
    ),
    titleLarge = TextStyle(
        fontSize   = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color      = TextPrimary
    ),
    titleMedium = TextStyle(
        fontSize   = 16.sp,
        fontWeight = FontWeight.Medium,
        color      = TextPrimary
    ),
    bodyLarge = TextStyle(
        fontSize   = 16.sp,
        fontWeight = FontWeight.Normal,
        color      = TextPrimary,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontSize   = 14.sp,
        fontWeight = FontWeight.Normal,
        color      = TextPrimary,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontSize   = 12.sp,
        fontWeight = FontWeight.Normal,
        color      = TextSecondary
    ),
    labelSmall = TextStyle(
        fontSize   = 11.sp,
        fontWeight = FontWeight.Normal,
        color      = TextTimestamp,
        letterSpacing = 0.sp
    )
)
