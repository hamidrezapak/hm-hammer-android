package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = DarkCardElevated,
    onPrimaryContainer = NeonCyan,
    secondary = NeonEmerald,
    onSecondary = Color.Black,
    secondaryContainer = Color(0x2600FFA3),
    onSecondaryContainer = NeonEmerald,
    tertiary = BrightGold,
    onTertiary = Color.Black,
    background = DarkNavyBg,
    onBackground = TextPrimary,
    surface = DarkCardSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkCardElevated,
    onSurfaceVariant = TextSecondary,
    outline = BorderStrokeColor,
    error = LaserCoral,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

