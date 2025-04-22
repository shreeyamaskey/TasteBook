package com.sm.tastebook.presentation.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sm.tastebook.data.common.datastore.UserSettings
import com.sm.tastebook.data.common.datastore.toUserSettings
import com.sm.tastebook.domain.user.repository.UserRepository
import com.sm.tastebook.domain.user.usecases.LogInUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.sm.tastebook.data.common.util.Result
import com.sm.tastebook.domain.user.model.UserAuthResultData


class LoginViewModel(
    private val logInUseCase: LogInUseCase,
    private val dataStore: DataStore<UserSettings>
) : ViewModel(){

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun logIn() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            val authResultData = logInUseCase(uiState.email, uiState.password)

            uiState = when (authResultData) {
                is Result.Error -> {
                    uiState.copy(
                        isLoading = false,
                        errorMessage = authResultData.message
                    )
                }

                is Result.Success -> {
                    println("Debug: Login success, token: ${authResultData.data?.token}")
                    println("Debug: Login success, userId: ${authResultData.data?.id}")
                    saveUserData(authResultData.data!!)
                    uiState.copy(
                        isLoading = false,
                        isLoggedIn = true
                    )
                }
            }
        }
    }

    fun onLoginClick() {
        if (uiState.email.isBlank() || uiState.password.isBlank()) {
            uiState = uiState.copy(errorMessage = "Email and password cannot be empty.")
            return
        }
        logIn()
    }

    fun onEmailChange(value: String) {
        uiState = uiState.copy(email = value)
    }

    fun onPasswordChange(value: String) {
        uiState =  uiState.copy(password = value)
    }

    private fun saveUserData(userData: UserAuthResultData) {
        viewModelScope.launch {
            dataStore.updateData { 
                UserSettings(
                    id = userData.id,
                    firstName = userData.firstName,
                    lastName = userData.lastName,
                    username = userData.username,
                    email = userData.email,
                    avatar = userData.avatar,
                    token = userData.token
                )
            }
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false
)
