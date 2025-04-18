package com.sm.tastebook.presentation.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sm.tastebook.data.common.datastore.UserSettings
import com.sm.tastebook.data.common.datastore.toUserSettings
import com.sm.tastebook.domain.user.model.UserAuthResultData
import com.sm.tastebook.domain.user.usecases.SignUpUseCase

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.sm.tastebook.data.common.util.Result


class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase,
    private val dataStore: DataStore<UserSettings>
) : ViewModel() {

    var uiState by mutableStateOf(SignUpUiState())
        private set

    fun onSignUpClick() {
        if(uiState.password != uiState.confirmPassword) {
            uiState = uiState.copy(errorMessage = "Passwords do not match. Please try again")
            return
        }
        signUp() // Call the use case which will validate further.
    }

    fun signUp() {
        viewModelScope.launch {
            uiState = uiState.copy(isAuthenticating = true)

            try {
                val userAuthResultData = signUpUseCase(
                    uiState.firstName,
                    uiState.lastName,
                    uiState.username,
                    uiState.email,
                    uiState.password
                )

                uiState = when (userAuthResultData) {
                    is Result.Error -> {
                        uiState.copy(
                            isAuthenticating = false,
                            errorMessage = userAuthResultData.message
                        )
                    }

                    is Result.Success -> {
                        dataStore.updateData {
                            userAuthResultData.data!!.toUserSettings()
                        }
                        uiState.copy(
                            isAuthenticating = false,
                            isSignedUp = true
                        )
                    }
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isAuthenticating = false,
                    errorMessage = "Error: ${e.message ?: "Unknown error occurred"}"
                )
            }
        }
    }

    // Functions to update individual fields:
    fun onFirstNameChange(value: String) {
        uiState = uiState.copy(firstName = value)
    }

    fun onLastNameChange(value: String) {
        uiState = uiState.copy(lastName = value)
    }

    fun onUsernameChange(value: String) {
        uiState = uiState.copy(username = value)
    }

    fun onEmailChange(value: String) {
        uiState = uiState.copy(email = value)
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value)
    }

    fun onConfirmPasswordChange(value: String) {
        uiState = uiState.copy(confirmPassword = value)
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
    val isAuthenticating: Boolean = false,
    val isSignedUp: Boolean = false
)

