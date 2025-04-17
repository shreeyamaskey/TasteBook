package com.sm.tastebook.di

import com.sm.tastebook.presentation.user.LoginViewModel
import com.sm.tastebook.presentation.user.SignUpViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SignUpViewModel(get()) }
    viewModel { LoginViewModel(get()) }
}