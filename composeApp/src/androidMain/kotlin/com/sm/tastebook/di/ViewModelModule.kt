package com.sm.tastebook.di

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.sm.tastebook.MainActivityViewModel
import com.sm.tastebook.data.common.datastore.UserSettingsSerializer
import com.sm.tastebook.presentation.user.LoginViewModel
import com.sm.tastebook.presentation.user.SignUpViewModel
import kotlinx.serialization.serializer
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SignUpViewModel(get(), get()) }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { MainActivityViewModel(get()) }

    single {
        DataStoreFactory.create(
            serializer = UserSettingsSerializer,
            produceFile = {
                androidContext().dataStoreFile(
                    fileName = "app_user_settings"
                )
            }
        )
    }
}