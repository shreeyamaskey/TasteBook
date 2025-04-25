package com.sm.tastebook.presentation.home

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sm.tastebook.data.common.datastore.UserSettings
import com.sm.tastebook.data.common.util.Result
import com.sm.tastebook.domain.recipe.repository.RecipeRepository
import com.sm.tastebook.presentation.recipe.RecipeUiModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WelcomeViewModel(
    private val recipeRepository: RecipeRepository,
    private val dataStore: DataStore<UserSettings>
) : ViewModel() {

    private val _state = MutableStateFlow(WelcomeUiState())
    val state: StateFlow<WelcomeUiState> = _state.asStateFlow()

    init {
        loadUserName()
        loadFeaturedRecipe()
    }

    private fun loadUserName() {
        viewModelScope.launch {
            dataStore.data.collect { settings ->
                _state.update { it.copy(
                    userName = settings.firstName.ifBlank { "Guest" }
                )}
            }
        }
    }

    private fun loadFeaturedRecipe() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                val settings = dataStore.data.first()
                // Get creator's recipe (creator has ID 1)
                when (val result = recipeRepository.getRecipesByPublisher(1)) {
                    is Result.Success -> {
                        result.data?.let { recipes ->
                            if (recipes.isNotEmpty()) {
                                val featured = recipes.first()
                                _state.update { it.copy(
                                    isLoading = false,
                                    featuredRecipe = RecipeUiModel(
                                        id = featured.recipeId,
                                        title = featured.recipeTitle,
                                        description = featured.recipeDesc ?: "",
                                        imageUrl = featured.imageUrl ?: "",
                                        ingredients = featured.ingredients?.map { ing -> ing.ingredientName } ?: emptyList(),
                                        instructions = listOf(featured.preparationSteps ?: ""),
                                        isSaved = featured.savesCount > 0
                                    )
                                )}
                            } else {
                                _state.update { it.copy(isLoading = false) }
                            }
                        } ?: _state.update { it.copy(isLoading = false) }
                    }
                    is Result.Error -> {
                        _state.update { it.copy(
                            isLoading = false,
                            error = result.message
                        )}
                    }
                }
            } catch (e: Exception) {
                println("Debug: Exception in loadFeaturedRecipe: ${e.message}")
                _state.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )}
            }
        }
    }

    fun onSaveRecipe(recipeId: Int) {
        viewModelScope.launch {
            // Implement save recipe functionality
            _state.update { currentState ->
                currentState.copy(
                    featuredRecipe = currentState.featuredRecipe?.copy(
                        isSaved = true
                    )
                )
            }
        }
    }
}

data class WelcomeUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val featuredRecipe: RecipeUiModel? = null,
    val error: String? = null
)