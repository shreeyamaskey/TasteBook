package com.sm.tastebook.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
actual fun getPlatformColors(): PlatformColors {
    // Explicitly define the colors for Android to ensure they display correctly
    return PlatformColors(
        primary = Color(0xff082d10),   // Dark green - explicitly defined for Android
        secondary = Color(0xff894827),  // Brown - explicitly defined for Android
        background = Color(0xffa08ea5)  // Lavender - explicitly defined for Android
    )
}