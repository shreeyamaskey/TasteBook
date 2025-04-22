package com.sm.tastebook.di

import com.sm.tastebook.data.common.util.provideDispatcher
import com.sm.tastebook.data.inventory.InventoryRepositoryImpl
import com.sm.tastebook.data.inventory.InventoryService
import com.sm.tastebook.data.user.AuthService
import com.sm.tastebook.data.user.UserRepositoryImpl
import com.sm.tastebook.domain.user.repository.UserRepository
import com.sm.tastebook.domain.user.usecases.LogInUseCase
import com.sm.tastebook.domain.user.usecases.SignUpUseCase
import com.sm.tastebook.data.recipe.RecipeService
import com.sm.tastebook.data.recipe.RecipeRepositoryImpl
import com.sm.tastebook.domain.inventory.repository.InventoryRepository
import com.sm.tastebook.domain.inventory.usecases.AddInventoryItemUseCase
import com.sm.tastebook.domain.inventory.usecases.DeleteInventoryItemUseCase
import com.sm.tastebook.domain.inventory.usecases.GetInventoryUseCase
import com.sm.tastebook.domain.recipe.repository.RecipeRepository
import com.sm.tastebook.domain.recipe.usecases.RecipeAddUseCase
import org.koin.dsl.module


private val userAuthModule = module {
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    factory { AuthService() }
    factory { SignUpUseCase() }
    factory { LogInUseCase() }
}

private val recipeModule = module {
    single<RecipeRepository> { RecipeRepositoryImpl(get(), get()) }
    factory { RecipeService() }
    factory { RecipeAddUseCase() }
}

private val inventoryModule = module {
    // Add this line
    single<InventoryRepository> { InventoryRepositoryImpl(get(), get()) }

    factory { InventoryService() }
    factory { GetInventoryUseCase() }
    factory { AddInventoryItemUseCase() }
    factory { DeleteInventoryItemUseCase() }
}


private val utilityModule = module {
    factory { provideDispatcher() }
}

// Update the shared modules list to include the network module
fun getSharedModules() = listOf(
    userAuthModule, 
    recipeModule, 
    utilityModule, 
    inventoryModule
)