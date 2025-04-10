package com.sm.tastebook.presentation.user

import androidx.lifecycle.ViewModel
import com.sm.tastebook.domain.user.model.User

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class SignUpViewModel() : ViewModel() {

    fun onEmptyFieldsError() {
        _uiState.update { it.copy(errorMessage = "Please fill in all fields") }
    }

    fun saveFirstName(name: String) {
        // Store the first name in the UI state
        _uiState.update { it.copy(firstName = name) }
    }

    fun getStoredFirstName(): String = _uiState.value.firstName


    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState

    fun onFirstNameChange(value: String) {
        _uiState.update { it.copy(firstName = value) }
    }

    fun onLastNameChange(value: String) {
        _uiState.update { it.copy(lastName = value) }
    }

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value) }
    }

    fun onSignUpClick() {
        val currentState = _uiState.value
        val validationResult = validateSignUp(
            email = currentState.email,
            password = currentState.password,
            confirmPassword = currentState.confirmPassword
        )

        _uiState.update { it.copy(errorMessage = validationResult.errorMessage) }

        if (validationResult.isValid) {
            // Later, you’ll call a repository or use-case to create a new user in the DB.
            // For now, we can just simulate success:
            println("Sign up successful! (Simulated)")
        }
    }

    private fun validateSignUp(
        email: String,
        password: String,
        confirmPassword: String
    ): ValidationResult {
        // Basic checks:
        if (!email.contains("@") || !email.contains(".com")) {
            return ValidationResult(isValid = false, errorMessage = "Invalid email address.")
        }
        if (password.length < 6) {
            return ValidationResult(isValid = false, errorMessage = "Password must be at least 6 characters.")
        }
        if (password != confirmPassword) {
            return ValidationResult(isValid = false, errorMessage = "Passwords do not match.")
        }
        return ValidationResult(isValid = true)
    }
}

data class SignUpUiState(
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isSignedUp: Boolean = false
)
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)
