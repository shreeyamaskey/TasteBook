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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.sm.tastebook.presentation.theme.TasteBookBackground
import androidx.compose.ui.graphics.Color


@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel,
    onBackClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onNavigateToHome: () -> Unit,
    ) {
    TasteBookBackground {

        val uiState = viewModel.uiState
        val context = LocalContext.current

        Column(
            modifier = Modifier.fillMaxSize(),
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

            Spacer(modifier = Modifier.height(16.dp))

            // Green box containing login form
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Welcome text
                    Text(
                        text = "Welcome to sharing recipes",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // White box containing login fields
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Username field
                            OutlinedTextField(
                                value = uiState.email,
                                onValueChange = onEmailChange,
                                label = { Text("Email:", color = Color.White) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.secondary),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = Color.Black,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            // Password field
                            OutlinedTextField(
                                value = uiState.password,
                                onValueChange = onPasswordChange,
                                label = { Text("Password:", color = Color.White) },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.secondary),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = Color.Black,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))

                            // Login button
                            Button(
                                onClick = onLoginClick,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = 8.dp),
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                enabled = !uiState.isLoading
                            ) {
                                if (uiState.isLoading) {
                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                                } else {
                                    Text(text = "Log in")
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            LaunchedEffect(
                key1 = uiState.isLoggedIn,
                key2 = uiState.errorMessage,
                block = {
                    if (uiState.isLoggedIn) {
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
