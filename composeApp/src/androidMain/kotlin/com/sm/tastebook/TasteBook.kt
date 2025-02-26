package com.sm.tastebook

import android.app.Application
import com.sm.tastebook.di.initKoin
import org.koin.android.ext.koin.androidContext

class TasteBook: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@TasteBook)
        }
    }
}