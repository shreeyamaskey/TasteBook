package com.sm.tastebook.presentation.user

import com.sm.tastebook.domain.user.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel{
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun onLoginClick() {
        val currentState = _uiState.value

        // Simple validation: both fields must not be empty
        if (currentState.username.isBlank() || currentState.password.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Username and password cannot be empty.")
            }
            return
        }

        // For now, just simulate a successful login
        // In the future, you'd check credentials against a DB or server
        println("Login successful! (Simulated)")

        // Clear error message on success
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }
}

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false
)
