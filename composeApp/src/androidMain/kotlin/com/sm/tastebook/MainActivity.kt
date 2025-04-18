package com.sm.tastebook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sm.tastebook.presentation.App
import com.sm.tastebook.presentation.theme.TasteBookTheme
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : ComponentActivity()
 {
    private val viewModel: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TasteBookTheme {
                val token = viewModel.authState.collectAsStateWithLifecycle(initialValue = null)
                App(token.value)
            }
        }
    }
}