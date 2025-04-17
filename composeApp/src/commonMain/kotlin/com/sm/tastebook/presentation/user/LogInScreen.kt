package com.sm.tastebook.presentation.user

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.material3.icons.Icons
//import androidx.compose.material3.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.sm.tastebook.presentation.theme.TasteBookBackground


@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onBackClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    uiState: LoginUiState,
    ) {
    TasteBookBackground {

        val context = LocalContext.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
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
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Log in",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Log in")
            }

            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = error)
            }
        }
    }

}
