package com.sm.tastebook.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TasteBookColorScheme = lightColorScheme(
    background = Color(0xA08EA5),
    primary = Color(0x082D10),
    secondary = Color(0x894827),
)

@Composable
fun TasteBookTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TasteBookColorScheme,
        typography = MaterialTheme.typography, // or a custom Typography if you have one
        content = content
    )
}
