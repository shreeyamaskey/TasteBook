package com.sm.tastebook.presentation.recipe

import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sm.tastebook.data.common.datastore.UserSettings
import com.sm.tastebook.domain.recipe.usecases.RecipeAddUseCase
import com.sm.tastebook.domain.user.model.UserAuthResultData
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RecipeAddViewModel(
    private val userSettings: DataStore<UserSettings>,
    private val context: Context
) : ViewModel(), KoinComponent {
    private val recipeAddUseCase: RecipeAddUseCase by inject()

    // 1) Track logged‑in user
    private val _userState = MutableStateFlow<UserAuthResultData?>(null)
    val userState: StateFlow<UserAuthResultData?> = _userState.asStateFlow()

    // 2) Screen UI state
    private val _uiState = MutableStateFlow(RecipeAddUiState())
    val uiState: StateFlow<RecipeAddUiState> = _uiState.asStateFlow()

    // 3) Current ingredient in the little form
    private val _currentIngredient = MutableStateFlow(IngredientState())
    val currentIngredient: StateFlow<IngredientState> = _currentIngredient.asStateFlow()

    init {
        // load the user from DataStore once
        viewModelScope.launch {
            userSettings.data.collectLatest { settings ->
                if (settings.id != -1 && settings.token.isNotBlank()) {
                    _userState.value = UserAuthResultData(
                        id = settings.id,
                        firstName = settings.firstName,
                        lastName = settings.lastName,
                        username = settings.username,
                        email = settings.email,
                        avatar = settings.avatar,
                        token = settings.token
                    )
                    println("User loaded from DataStore: ID=${settings.id}, Token=${settings.token.take(10)}...")
                } else {
                    println("No valid user found in DataStore: ID=${settings.id}, Token empty=${settings.token.isBlank()}")
                }
            }
        }
    }

    // ——— Field updaters ———
    fun onTitleChange(v: String)           = _uiState.update { it.copy(title = v) }
    fun onDescriptionChange(v: String)     = _uiState.update { it.copy(description = v) }
    fun onPreparationStepsChange(v: String)= _uiState.update { it.copy(preparationSteps = v) }

    fun onIngredientNameChange(v: String)  = _currentIngredient.update { it.copy(name = v) }
    fun onIngredientAmountChange(v: String)= _currentIngredient.update { it.copy(amount = v) }
    fun onIngredientMeasurementChange(v:String)= _currentIngredient.update { it.copy(measurement = v) }

    fun addIngredient() {
        val ing = _currentIngredient.value
        if (ing.name.isNotBlank() && ing.amount.isNotBlank()) {
            _uiState.update { it.copy(ingredients = it.ingredients + ing) }
            _currentIngredient.value = IngredientState()
        }
    }
    fun removeIngredient(i: Int) {
        _uiState.update {
            val list = it.ingredients.toMutableList().apply { if (i in indices) removeAt(i) }
            it.copy(ingredients = list)
        }
    }

    // ——— Image Uri handling ———
    fun setMainImageUri(uri: Uri) {
        _uiState.update { it.copy(mainImageUri = uri) }
    }
    fun addAdditionalImageUri(uri: Uri) {
        _uiState.update { it.copy(additionalImageUris = it.additionalImageUris + uri) }
    }
    fun removeAdditionalImageUri(i: Int) {
        _uiState.update {
            val list = it.additionalImageUris.toMutableList().apply { if (i in indices) removeAt(i) }
            it.copy(additionalImageUris = list)
        }
    }

    // ——— Submit ———
    fun submitRecipe() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val user = _userState.value
            if (user == null) {
                _uiState.update { it.copy(error = "Log in first", isLoading = false) }
                println("Recipe submission failed: No user logged in")
                return@launch
            }
            
            println("Submitting recipe with user ID: ${user.id}, Token: ${user.token.take(10)}...")

            // simple front‑end validation
            val currentState = _uiState.value
            when {
                currentState.title.isBlank() -> {
                    _uiState.update { it.copy(error = "Title required", isLoading = false) }
                    return@launch
                }
                currentState.description.isBlank() -> {
                    _uiState.update { it.copy(error = "Description required", isLoading = false) }
                    return@launch
                }
                currentState.preparationSteps.isBlank() -> {
                    _uiState.update { it.copy(error = "Steps required", isLoading = false) }
                    return@launch
                }
                currentState.ingredients.isEmpty() -> {
                    _uiState.update { it.copy(error = "Add ≥1 ingredient", isLoading = false) }
                    return@launch
                }
                currentState.mainImageUri == null -> {
                    _uiState.update { it.copy(error = "Main image required", isLoading = false) }
                    return@launch
                }
            }

            try {
                // convert Uri→ByteArray
                val mainBytes = context.contentResolver.openInputStream(_uiState.value.mainImageUri!!)!!
                    .use { it.readBytes() }
                println("Main image converted to ${mainBytes.size} bytes")
                
                val addBytes = _uiState.value.additionalImageUris.map { uri ->
                    context.contentResolver.openInputStream(uri)!!.use { it.readBytes() }
                }
                println("Additional images converted: ${addBytes.size} images")

                // Make sure token is properly formatted
                val formattedToken = if (!user.token.startsWith("Bearer ")) "Bearer ${user.token}" else user.token
                println("Using token format: ${formattedToken.take(16)}...")
                
                // call your use‑case
                when (val res = recipeAddUseCase(
                    token = formattedToken, // Use formatted token
                    recipeTitle = _uiState.value.title,
                    recipeDesc = _uiState.value.description,
                    preparationSteps = _uiState.value.preparationSteps,
                    ingredients = _uiState.value.ingredients.map { ing ->
                        Triple(ing.name, ing.amount.toDoubleOrNull() ?: 0.0, ing.measurement)
                    },
                    mainImage = mainBytes,
                    additionalImages = addBytes
                )) {
                    is com.sm.tastebook.data.common.util.Result.Success -> {
                        _uiState.update { it.copy(isSuccess = true, isLoading = false) }
                    }
                    is com.sm.tastebook.data.common.util.Result.Error -> {
                        _uiState.update { it.copy(error = res.message, isLoading = false) }
                    }
                }
            } catch (e: Throwable) {
                println("Recipe submission error: ${e.message}")
                _uiState.update { it.copy(error = e.message ?: "Unknown error", isLoading = false) }
            }
        }
    }
}

data class RecipeAddUiState(
    val title: String                   = "",
    val description: String             = "",
    val preparationSteps: String        = "",
    val ingredients: List<IngredientState> = emptyList(),

    // now hold Uris for Coil preview
    val mainImageUri: Uri?              = null,
    val additionalImageUris: List<Uri>  = emptyList(),

    val isLoading: Boolean              = false,
    val isSuccess: Boolean              = false,
    val error: String?                  = null
)

data class IngredientState(
    val name: String    = "",
    val amount: String  = "",
    val measurement: String = ""
)
