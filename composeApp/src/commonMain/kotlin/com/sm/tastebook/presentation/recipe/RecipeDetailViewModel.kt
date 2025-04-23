package com.sm.tastebook.presentation.recipe

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sm.tastebook.data.common.datastore.UserSettings
import com.sm.tastebook.domain.recipe.repository.RecipeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RecipeDetailViewModel(
    private val recipeRepository: RecipeRepository,
    private val dataStore: DataStore<UserSettings>
) : ViewModel() {

    private val _state = MutableStateFlow(RecipeDetailUiState())
    val state: StateFlow<RecipeDetailUiState> = _state.asStateFlow()

    fun loadRecipe(recipeId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                val settings = dataStore.data.first()
                println("Debug: Loading recipe $recipeId with token: ${settings.token.take(10)}...")
                val result = recipeRepository.getRecipeById(settings.token, recipeId)
                
                when (result) {
                    is com.sm.tastebook.data.common.util.Result.Success -> {
                        result.data?.let { recipe ->
                            println("Debug: Recipe loaded successfully: ${recipe.recipeTitle}")
                            println("Debug: Description: ${recipe.recipeDesc}")
                            println("Debug: Ingredients count: ${recipe.ingredients.size}")
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    title = recipe.recipeTitle,
                                    description = recipe.recipeDesc,
                                    publisherName = "Publisher #${recipe.publisherId}", // Using ID instead of name
                                    publishDate = formatDate(recipe.publishedAt), // Using publishedAt instead of createdAt
                                    imageUrl = recipe.imageUrl,
                                    ingredients = recipe.ingredients.map { ing -> 
                                        "${ing.ingredientName}: ${ing.quantity} ${ing.measurementUnit}"
                                    },
                                    preparationSteps = recipe.preparationSteps
                                )
                            }
                        }
                    }
                    is com.sm.tastebook.data.common.util.Result.Error -> {
                        _state.update { 
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                println("Debug: Error loading recipe: ${e.message}")
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }

    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            .format(Date(timestamp))
    }
}

data class RecipeDetailUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val title: String = "",
    val description: String = "",
    val publisherName: String = "",
    val publishDate: String = "",
    val imageUrl: String? = null,
    val ingredients: List<String> = emptyList(),
    val preparationSteps: String = ""
)