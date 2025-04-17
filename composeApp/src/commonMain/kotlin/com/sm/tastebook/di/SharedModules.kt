package com.sm.tastebook.di

import com.sm.tastebook.data.common.util.provideDispatcher
import com.sm.tastebook.data.user.AuthService
import com.sm.tastebook.data.user.UserRepositoryImpl
import com.sm.tastebook.domain.user.repository.UserRepository
import com.sm.tastebook.domain.user.usecases.LogInUseCase
import com.sm.tastebook.domain.user.usecases.SignUpUseCase
import org.koin.dsl.module


private val userAuthModule = module {
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    factory { AuthService() }
    factory { SignUpUseCase() }
    factory { LogInUseCase() }
}

private val utilityModule = module {
    factory { provideDispatcher() }
}

fun getSharedModules() = listOf(userAuthModule, utilityModule)