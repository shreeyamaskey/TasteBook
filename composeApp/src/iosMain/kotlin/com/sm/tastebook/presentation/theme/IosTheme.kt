package com.sm.tastebook.presentation.theme

import androidx.compose.runtime.Composable

@Composable
actual fun getPlatformColors(): PlatformColors {
    // For iOS, we'll use the default colors defined in the common theme
    return PlatformColors()
}