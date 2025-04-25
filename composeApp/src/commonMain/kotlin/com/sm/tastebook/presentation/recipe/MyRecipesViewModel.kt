package com.sm.tastebook.presentation.recipe

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sm.tastebook.data.common.datastore.UserSettings
import com.sm.tastebook.data.common.util.Result
import com.sm.tastebook.domain.recipe.repository.RecipeRepository
import com.sm.tastebook.domain.recipe.model.RecipeResponseData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class MyRecipesViewModel(
    private val recipeRepository: RecipeRepository,
    private val dataStore: DataStore<UserSettings>
) : ViewModel() {

    private val _state = MutableStateFlow(MyRecipesUiState(isLoading = true))
    val state: StateFlow<MyRecipesUiState> = _state.asStateFlow()

    init {
        loadMyRecipes()
    }


    private fun loadMyRecipes() {
        viewModelScope.launch {
            // Show loading
            _state.value = MyRecipesUiState(isLoading = true)

            // 1) read user settings once
            val settings = dataStore.data.first()
            if (settings.id <= 0 || settings.token.isBlank()) {
                _state.value = MyRecipesUiState(
                    isLoading = false,
                    error = "Please log in first"
                )
                return@launch
            }

            // 2) fetch
            when (val result = recipeRepository.getAllRecipes(settings.token, settings.id)) {
                is Result.Success -> {
                    // Add debug print
                    println("Debug: Received recipes: ${result.data?.size}")
                    result.data?.forEach { 
                        println("Debug: Recipe: ${it.recipeTitle}, Image: ${it.imageUrl}")
                    }
                    
                    // 3) map domain → UI
                    val uiModels = result.data?.map { dto ->
                        RecipeUiModel(
                            id            = dto.recipeId,
                            title         = dto.recipeTitle,
                            description   = dto.recipeDesc,
                            imageUrl      = dto.imageUrl,
                            ingredients   = dto.ingredients.map { it.ingredientName },
                            instructions  = listOf(dto.preparationSteps),
                            isSaved      = dto.savesCount > 0
                        )
                    } ?: emptyList()
                    
                    // 4) push success
                    _state.value = MyRecipesUiState(
                        isLoading = false,
                        recipes   = uiModels
                    )
                }
                is Result.Error -> {
                    // 4b) push error
                    _state.value = MyRecipesUiState(
                        isLoading = false,
                        error     = result.message
                    )
                }
            }
        }
    }

    fun onSaveRecipe(recipeId: Int) {
        viewModelScope.launch {
            try {
                dataStore.data.collect { settings ->
                    if (settings.token.isNotEmpty()) {
                        _state.update { state ->
                            val updatedRecipes = state.recipes.map { recipe ->
                                if (recipe.id == recipeId) {
                                    recipe.copy(isSaved = true)
                                } else {
                                    recipe
                                }
                            }
                            state.copy(recipes = updatedRecipes)
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun refreshRecipes() {
        loadMyRecipes()
    }
}

data class RecipeUiModel(
    val id: Int,
    val title: String,
    val description: String = "",
    val imageUrl: String? = null,
    val ingredients: List<String> = emptyList(),
    val instructions: List<String> = emptyList(),
    val isSaved: Boolean = false
)

data class MyRecipesUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val recipes: List<RecipeUiModel> = emptyList()
)

