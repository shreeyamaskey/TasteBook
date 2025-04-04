// Background.kt
package com.sm.tastebook.presentation.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.painter.BitmapPainter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize


@Composable
fun TasteBookBackground(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Solid background color from your MaterialTheme
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )
        // Repeating pattern overlay
        TiledBackground(
            imageName = "squiggly-line.png", // Ensure the file name matches exactly
            modifier = Modifier.fillMaxSize()
        )
        // Place the content on top
        Column(modifier = Modifier.fillMaxSize(), content = content)
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun TiledBackground(
    imageName: String, // e.g., "squiggly-line.png"
    modifier: Modifier = Modifier
) {
    // Load the resource file from commonMain/resources
    val resource = resource(imageName)
    // Read the image bytes
    val bytes = resource.readBytes()
    // Decode the bytes into an ImageBitmap
    val bitmap: ImageBitmap = loadImageBitmap(bytes.inputStream())

    BoxWithConstraints(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val tileWidth = bitmap.width.toFloat()
            val tileHeight = bitmap.height.toFloat()
            var y = 0f
            while (y < size.height) {
                var x = 0f
                while (x < size.width) {
                    drawImage(
                        image = bitmap,
                        topLeft = Offset(x, y)
                    )
                    x += tileWidth
                }
                y += tileHeight
            }
        }
    }
}

