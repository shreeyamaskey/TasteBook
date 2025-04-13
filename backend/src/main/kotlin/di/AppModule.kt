package server.com.di

import org.koin.dsl.module
import server.com.dao.user.UserDao
import server.com.dao.user.UserDaoImpl
import server.com.repository.user.UserRepository
import server.com.repository.user.UserRepositoryImpl

val appModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<UserDao> { UserDaoImpl() }
}