package com.sm.tastebook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.sm.tastebook.app.App
import com.sm.tastebook.presentation.theme.TasteBookTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TasteBookTheme {
                App()
            }
        }
    }
}