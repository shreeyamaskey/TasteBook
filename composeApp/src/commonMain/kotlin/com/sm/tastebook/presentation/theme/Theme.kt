package com.sm.tastebook.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme

private val LightColorScheme = lightColorScheme(
    background = Color(0xffa08ea5), // Lavender
    primary = Color(0xff082d10),   // Dark green
    surface = Color.White,      // White
    secondary = Color(0xff894827),      // Brown
    onPrimary = Color.White,
    onSecondary = Color(0xff082d10), // Dark green
    onBackground = Color.White,
    onSurface = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8B4513),      // Brown for buttons
    secondary = Color(0xffa08ea5),    // Light pink/lavender
    background = Color(0xFF0B4B23),   // Dark green
    surface = Color(0xFF0B4B23),      // Dark green
    onPrimary = Color.White,
    onSecondary = Color(0xFF0B4B23),
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun TasteBookTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
