package com.chatapp.android.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary          = ChatGreen,
    onPrimary        = DarkBackground,
    primaryContainer = SentBubble,
    secondary        = AccentBlue,
    background       = DarkBackground,
    surface          = DarkSurface,
    surfaceVariant   = DarkSurface2,
    onBackground     = TextPrimary,
    onSurface        = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline          = TextTimestamp,
)

@Composable
fun ChatAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = ChatTypography,
        content     = content
    )
}
