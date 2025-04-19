package server.com.di

import org.koin.dsl.module
import server.com.dao.recipe.RecipeDao
import server.com.dao.recipe.RecipeDaoImpl
import server.com.dao.user.UserDao
import server.com.dao.user.UserDaoImpl
import server.com.repository.recipe.RecipeRepository
import server.com.repository.recipe.RecipeRepositoryImpl
import server.com.repository.user.UserRepository
import server.com.repository.user.UserRepositoryImpl

val appModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<UserDao> { UserDaoImpl() }
    single<RecipeRepository> { RecipeRepositoryImpl(get()) }
    single<RecipeDao> { RecipeDaoImpl() }
}