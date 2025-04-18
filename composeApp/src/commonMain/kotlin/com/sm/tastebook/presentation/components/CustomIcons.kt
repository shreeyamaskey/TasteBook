package com.sm.tastebook.presentation.components

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

object CustomIcons {
    val Grocery: ImageVector
        get() {
            if (_grocery != null) {
                return _grocery!!
            }
            _grocery = materialIcon(name = "Grocery") {
                materialPath {
                    // This is the path data for a grocery/food icon
                    // Using a simpler path for better compatibility
                    moveTo(7.0f, 18.0f)
                    horizontalLineTo(17.0f)
                    verticalLineTo(6.0f)
                    horizontalLineTo(7.0f)
                    verticalLineTo(18.0f)
                    close()
                    
                    // Bottle neck
                    moveTo(10.0f, 6.0f)
                    verticalLineTo(3.0f)
                    horizontalLineTo(14.0f)
                    verticalLineTo(6.0f)
                    
                    // Apple/fruit shape
                    moveTo(17.0f, 8.0f)
                    arcTo(4.0f, 4.0f, 0.0f, true, false, 17.0f, 16.0f)
                    arcTo(4.0f, 4.0f, 0.0f, true, false, 17.0f, 8.0f)
                }
            }
            return _grocery!!
        }
    private var _grocery: ImageVector? = null
}