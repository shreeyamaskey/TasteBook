
package com.sm.tastebook.presentation.inventory

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sm.tastebook.data.common.datastore.UserSettings
import com.sm.tastebook.data.common.util.Result
import com.sm.tastebook.domain.inventory.model.InventoryItemResponseData
import com.sm.tastebook.domain.inventory.usecases.*
import com.sm.tastebook.domain.user.model.UserAuthResultData
import kotlinx.coroutines.launch

class InventoryViewModel(
    private val userSettings: DataStore<UserSettings>,
    private val getInventoryUseCase: GetInventoryUseCase,
    private val addInventoryItemUseCase: AddInventoryItemUseCase,
    private val deleteInventoryItemUseCase: DeleteInventoryItemUseCase
) : ViewModel() {
    
    var uiState by mutableStateOf(InventoryUiState())
        private set

    private var userState by mutableStateOf<UserAuthResultData?>(null)

    init {
        viewModelScope.launch {
            println("Debug: ViewModel init started") // Debug log
            userSettings.data.collect { settings ->
                println("Debug: Settings received: id=${settings.id}, hasToken=${settings.token.isNotBlank()}") // Debug log
                if (settings.id != -1 && settings.token.isNotBlank()) {
                    userState = UserAuthResultData(
                        id = settings.id,
                        firstName = settings.firstName,
                        lastName = settings.lastName,
                        username = settings.username,
                        email = settings.email,
                        avatar = settings.avatar,
                        token = settings.token
                    )
                    println("Debug: User state updated, calling fetchInventory") // Debug log
                    fetchInventory()
                } else {
                    println("Debug: Invalid settings state") // Debug log
                }
            }
        }
    }

    private fun fetchInventory() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            
            val user = userState
            if (user == null) {
                println("Debug: User is null during fetch")
                uiState = uiState.copy(error = "Please log in first", isLoading = false)
                return@launch
            }

            try {
                val formattedToken = if (!user.token.startsWith("Bearer ")) "Bearer ${user.token}" else user.token
                println("Debug: Fetching inventory with token: $formattedToken")
                
                when (val result = getInventoryUseCase(formattedToken)) {
                    is Result.Success -> {
                        val items = result.data?.map { it.toUiModel() } ?: emptyList()
                        println("Debug: Fetch success, items: $items") // Debug log
                        uiState = uiState.copy(
                            items = items,
                            isLoading = false,
                            error = null
                        )
                    }
                    is Result.Error -> {
                        println("Debug: Fetch error: ${result.message}")
                        uiState = uiState.copy(
                            error = result.message,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                println("Debug: Fetch exception: ${e.message}")
                e.printStackTrace()
                uiState = uiState.copy(
                    error = e.message ?: "Unknown error",
                    isLoading = false
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        uiState = uiState.copy(searchQuery = query)
    }

    fun addItem(name: String, quantity: Double, unit: String, expiryDate: Long?) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            
            if (userState == null) {
                uiState = uiState.copy(error = "Please log in first", isLoading = false)
                return@launch
            }

            try {
                val formattedToken = if (!userState!!.token.startsWith("Bearer ")) "Bearer ${userState!!.token}" else userState!!.token
                println("Debug: Fetching inventory with token: $formattedToken")

                when (val result = addInventoryItemUseCase(formattedToken, name, quantity, unit, expiryDate)) {
                    is Result.Success -> {
                        fetchInventory()
                    }
                    is Result.Error -> {
                        uiState = uiState.copy(error = result.message, isLoading = false)
                    }
                }
            } catch (e: Exception) {
                uiState = uiState.copy(error = e.message ?: "Unknown error", isLoading = false)
            }
        }
    }

    fun deleteItem(id: Int) {
        viewModelScope.launch {
            val user = userState
            if (user == null) {
                uiState = uiState.copy(error = "Please log in first")
                return@launch
            }
            
            val formattedToken = if (!user.token.startsWith("Bearer ")) "Bearer ${user.token}" else user.token
            
            when (val result = deleteInventoryItemUseCase(formattedToken, id)) {
                is Result.Success -> {
                    fetchInventory()
                }
                is Result.Error -> {
                    uiState = uiState.copy(error = result.message)
                }
            }
        }
    }

    private fun InventoryItemResponseData.toUiModel(): InventoryItemUiModel {
        return InventoryItemUiModel(
            id = id,
            userId = userId,
            ingredientName = ingredientName,
            quantity = quantity,
            measurementUnit = measurementUnit,
            expiryDate = expiryDate,
            dateAdded = dateAdded
        )
    }
}

data class InventoryUiState(
    val items: List<InventoryItemUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
) {
    val filteredItems: List<InventoryItemUiModel>
        get() = if (searchQuery.isBlank()) {
            items
        } else {
            items.filter { 
                it.ingredientName.contains(searchQuery, ignoreCase = true) 
            }
        }
}

data class InventoryItemUiModel(
    val id: Int,
    val userId: Int,
    val ingredientName: String,
    val quantity: Double,
    val measurementUnit: String,
    val expiryDate: Long? = null,
    val dateAdded: Long
)