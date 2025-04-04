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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import tastebook.composeapp.generated.resources.Res

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
//        TiledBackground(
//            painter = painterResource(Res.drawable.squiggly_lines), // Generated resource; ensure the file is named correctly.
//            modifier = Modifier.fillMaxSize()
//        )
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
//@Composable
//fun TiledBackground(
//    painter: Painter,
//    modifier: Modifier = Modifier
//) {
//    // Use the painter's intrinsic size as the tile dimensions.
//    // Note: If intrinsicSize is unspecified, you might need to provide a default size.
//    val tileWidth = if (painter.intrinsicSize.width.isFinite()) painter.intrinsicSize.width else 100f
//    val tileHeight = if (painter.intrinsicSize.height.isFinite()) painter.intrinsicSize.height else 100f
//
//    BoxWithConstraints(modifier = modifier) {
//        Canvas(modifier = Modifier.fillMaxSize()) {
//            var y = 0f
//            while (y < size.height) {
//                var x = 0f
//                while (x < size.width) {
//                    drawPainter(
//                        painter = painter,
//                        topLeft = Offset(x, y),
//                        size = Size(tileWidth, tileHeight)
//                    )
//                    x += tileWidth
//                }
//                y += tileHeight
//            }
//        }
//    }
//}
