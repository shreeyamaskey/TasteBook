package com.sm.tastebook

import android.app.Application
import com.sm.tastebook.di.getSharedModules
import com.sm.tastebook.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TasteBook: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@TasteBook)
            modules(viewModelModule + getSharedModules())
        }
    }
}