package com.sm.tastebook.presentation.user

import android.widget.Toast
import androidx.compose.foundation.background
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
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TasteBook header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "TasteBook", 
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            // Back button positioned to the left
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }

            // Green box containing all text fields
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.firstName,
                        onValueChange = onFirstNameChange,
                        label = { Text("First Name:", color = MaterialTheme.colorScheme.onPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedTextColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )

                    OutlinedTextField(
                        value = uiState.lastName,
                        onValueChange = onLastNameChange,
                        label = { Text("Last Name:", color = MaterialTheme.colorScheme.onPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedTextColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )

                    OutlinedTextField(
                        value = uiState.username,
                        onValueChange = onUsernameChange,
                        label = { Text("Username:", color = MaterialTheme.colorScheme.onPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedTextColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )

                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = onEmailChange,
                        label = { Text("Email:", color = MaterialTheme.colorScheme.onPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedTextColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )

                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = onPasswordChange,
                        label = { Text("Password:", color = MaterialTheme.colorScheme.onPrimary) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedTextColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )

                    OutlinedTextField(
                        value = uiState.confirmPassword,
                        onValueChange = onConfirmPasswordChange,
                        label = { Text("Confirm Password:", color = MaterialTheme.colorScheme.onPrimary) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedTextColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onSignUpClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        enabled = !uiState.isAuthenticating
                    ) {
                        if (uiState.isAuthenticating) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Text(text = "Make an account")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                }
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