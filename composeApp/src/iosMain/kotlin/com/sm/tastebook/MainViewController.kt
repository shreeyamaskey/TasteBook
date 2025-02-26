package com.sm.tastebook

import androidx.compose.ui.window.ComposeUIViewController
import com.sm.tastebook.app.App
import com.sm.tastebook.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }