package com.sm.tastebook.presentation.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Add these imports
// import android.content.Context
// import android.content.Intent
// import android.net.Uri
// import android.provider.MediaStore
// import androidx.activity.compose.rememberLauncherForActivityResult
// import androidx.activity.result.contract.ActivityResultContracts

class RecipeAddViewModel : ViewModel() {

    // State for the recipe add screen
    private val _uiState = MutableStateFlow(RecipeAddUiState())
    val uiState: StateFlow<RecipeAddUiState> = _uiState.asStateFlow()

    // Ingredient state for the current ingredient being added
    private val _currentIngredient = MutableStateFlow(IngredientState())
    val currentIngredient: StateFlow<IngredientState> = _currentIngredient.asStateFlow()

    // Functions to update the recipe fields
    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    // Functions to update the current ingredient being edited
    fun onIngredientNameChange(name: String) {
        _currentIngredient.update { it.copy(name = name) }
    }

    fun onIngredientAmountChange(amount: String) {
        _currentIngredient.update { it.copy(amount = amount) }
    }

    fun onIngredientMeasurementChange(measurement: String) {
        _currentIngredient.update { it.copy(measurement = measurement) }
    }

    // Add the current ingredient to the list
    fun addIngredient() {
        val ingredient = _currentIngredient.value
        if (ingredient.name.isNotBlank() && ingredient.amount.isNotBlank()) {
            _uiState.update { 
                it.copy(ingredients = it.ingredients + ingredient)
            }
            // Reset the current ingredient
            _currentIngredient.update { IngredientState() }
        }
    }

    // Remove an ingredient from the list
    fun removeIngredient(index: Int) {
        _uiState.update {
            val newList = it.ingredients.toMutableList()
            if (index in newList.indices) {
                newList.removeAt(index)
            }
            it.copy(ingredients = newList)
        }
    }

    // Photo handling
    fun addPhotoUrl(url: String) {
        if (url.isNotBlank()) {
            _uiState.update { 
                it.copy(photoUrls = it.photoUrls + url)
            }
        }
    }

    fun removePhotoUrl(index: Int) {
        _uiState.update {
            val newList = it.photoUrls.toMutableList()
            if (index in newList.indices) {
                newList.removeAt(index)
            }
            it.copy(photoUrls = newList)
        }
    }

    // Remove the Android-specific gallery picker methods
    // We'll handle image picking in the platform-specific UI layer instead
    
    // Submit the recipe
    fun submitRecipe() {
        viewModelScope.launch {
            // Set loading state
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // TODO: Implement the actual API call to save the recipe
                // For now, just simulate success
                _uiState.update { 
                    it.copy(
                        isSuccess = true,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = e.message ?: "Unknown error occurred",
                        isLoading = false
                    )
                }
            }
        }
    }
}

// State classes
data class RecipeAddUiState(
    val title: String = "",
    val description: String = "",
    val ingredients: List<IngredientState> = emptyList(),
    val photoUrls: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

data class IngredientState(
    val name: String = "",
    val amount: String = "",
    val measurement: String = ""
)