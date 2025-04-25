package com.sm.tastebook.presentation.recipe

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sm.tastebook.data.common.datastore.UserSettings
import com.sm.tastebook.domain.inventory.model.InventoryItemResponseData
import com.sm.tastebook.data.inventory.InventoryService
import com.sm.tastebook.domain.user.model.UserAuthResultData
import com.sm.tastebook.data.common.util.Result
import com.sm.tastebook.domain.recipe.repository.RecipeRepository
import com.sm.tastebook.domain.user.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RecipeDetailViewModel internal constructor(  // Mark constructor as internal
    private val recipeRepository: RecipeRepository,
    private val userRepository: UserRepository,
    private val inventoryService: InventoryService,
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
                    is Result.Success -> {
                        result.data?.let { recipe ->
                            // Get publisher name using UserRepository instead
                            val publisherResult = userRepository.getUserProfile(recipe.publisherId)
                            val publisherName = when (publisherResult) {
                                is com.sm.tastebook.data.common.util.Result.Success<UserAuthResultData> ->
                                    "${publisherResult.data?.firstName ?: "Unknown"} ${publisherResult.data?.lastName ?: ""}"
                                else -> "Publisher #${recipe.publisherId}"
                            }

                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    title = recipe.recipeTitle,
                                    description = recipe.recipeDesc,
                                    publisherName = publisherName,
                                    publishDate = formatDate(recipe.publishedAt),
                                    imageUrl = recipe.imageUrl,
                                    ingredients = recipe.ingredients.map { ing -> 
                                        "${ing.ingredientName}: ${ing.quantity} ${ing.measurementUnit}"
                                    },
                                    preparationSteps = recipe.preparationSteps
                                )
                            }
                        }
                    }
                    is Result.Error -> {
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
        // Convert seconds to milliseconds since the timestamp is in seconds
        val milliseconds = timestamp * 1000
        return SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            .format(Date(milliseconds))
    }

    fun generateGroceryList() {
        viewModelScope.launch {
            try {
                val settings = dataStore.data.first()
                // Get user's inventory
                val inventoryResult = inventoryService.getUserInventory(settings.token)
                
                if (inventoryResult.success && inventoryResult.data != null) {
                    val inventory = inventoryResult.data
                    val currentIngredients = _state.value.ingredients
                    
                    // Compare recipe ingredients with inventory
                    val groceryList = currentIngredients.mapNotNull { recipeIngredient ->
                        // Parse the recipe ingredient string (format: "name: amount unit")
                        val parts = recipeIngredient.split(":")
                        if (parts.size != 2) return@mapNotNull null
                        
                        val name = parts[0].trim()
                        val quantityParts = parts[1].trim().split(" ")
                        if (quantityParts.size != 2) return@mapNotNull null
                        
                        val amount = quantityParts[0].toDoubleOrNull() ?: return@mapNotNull null
                        val unit = quantityParts[1]
                        
                        // Check if ingredient exists in inventory with sufficient quantity
                        val inventoryItem = inventory.find { it.ingredientName.equals(name, ignoreCase = true) }
                        
                        if (inventoryItem == null || inventoryItem.quantity < amount) {
                            // If not in inventory or insufficient quantity, add to grocery list
                            val neededAmount = inventoryItem?.let { amount - it.quantity } ?: amount
                            Triple(name, neededAmount, unit)
                        } else null
                    }
                    
                    // Update UI state with grocery list
                    _state.update { it.copy(
                        groceryList = groceryList.map { (name, amount, unit) ->
                            "$name: $amount $unit"
                        }
                    ) }
                } else {
                    _state.update { it.copy(error = inventoryResult.errorMessage ?: "Failed to fetch inventory") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Error generating grocery list") }
            }
        }
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
    val preparationSteps: String = "",
    val groceryList: List<String> = emptyList()
)