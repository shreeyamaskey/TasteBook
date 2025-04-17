package com.sm.tastebook.presentation.user

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.sm.tastebook.presentation.theme.TasteBookBackground
import androidx.compose.material3.MaterialTheme


@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel,
    onBackClick: () -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onSignUpClick: () -> Unit
) {
    TasteBookBackground {
        // Collect UI state from the ViewModel
        val uiState = viewModel.uiState

        val context = LocalContext.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "TasteBook")

            Spacer(modifier = Modifier.height(16.dp))

            // A simple "back" arrow using an IconButton
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onBackClick() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }

            OutlinedTextField(
                value = uiState.firstName,
                onValueChange = onFirstNameChange,
                label = { Text("First name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.lastName,
                onValueChange = onLastNameChange,
                label = { Text("Last name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.username,
                onValueChange = onUsernameChange,
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onSignUpClick()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isAuthenticating // Disable button during authentication
            ) {
                if (uiState.isAuthenticating) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(text = "Make an account")
                }
            }

            // Show error message if any
            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = error)
            }

            if (uiState.isAuthenticating) {
                CircularProgressIndicator()
            }

            LaunchedEffect(
                key1 = uiState.isSignedUp,
                key2 = uiState.errorMessage,
                block = {
                    if (uiState.isSignedUp) {
                        onNavigateToHome()
                    }

                    if (uiState.errorMessage != null) {
                        Toast.makeText(context, uiState.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }

}