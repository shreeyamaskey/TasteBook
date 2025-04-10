package com.sm.tastebook.presentation.theme
//
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
// import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import tastebook.composeapp.generated.resources.Res

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import tastebook.composeapp.generated.resources.squiggly_line

import org.jetbrains.compose.resources.painterResource
/**
 * Global background composable that applies:
 * 1. A solid background color (from your Material 3 theme),
 * 2. A tiled overlay image,
 * 3. And then displays the [content] on top.
 */
@Composable
fun TasteBookBackground(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        // 1) Solid background color from your theme
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )
        
        // 2) Tiled overlay image
        TiledBackground(
            painter = painterResource(Res.drawable.squiggly_line),
            modifier = Modifier.fillMaxSize()
        )
        
        // 3) Foreground content
        Column(
            modifier = Modifier.fillMaxSize(),
            content = content
        )
    }
}

/**
 * This composable tiles a given [painter] across its full area using a Canvas.
 */
@Composable
fun TiledBackground(
    painter: Painter,
    modifier: Modifier = Modifier
) {
    val tileSize = 320.dp
    val spacing = 190.dp
    
    BoxWithConstraints(modifier = modifier) {
        val rows = (maxHeight / spacing).toInt() + 1
        val columns = (maxWidth / spacing).toInt() + 1
        
        // Start from a negative offset to remove the left padding
        for (row in 0 until rows) {
            for (column in -1 until columns) {  // Start from -1 to extend beyond left edge
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(tileSize)
                        .offset(x = (column * spacing.value).dp, y = (row * spacing.value).dp)
                        .alpha(0.5f),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}
