package com.ebydev.devpocket.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DevPocketColors = darkColorScheme(
    primary = Color(0xFFB5F36A),
    onPrimary = Color(0xFF1B260B),
    secondary = Color(0xFF9CC9FF),
    background = Color(0xFF0D1117),
    surface = Color(0xFF151B23),
    surfaceVariant = Color(0xFF202833),
)

@Composable
fun DevPocketTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DevPocketColors, content = content)
}
