package com.sm.tastebook.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LandingScreen(
    onSignUpClick: () -> Unit,
    onLogInClick: () -> Unit
) {
    // A simple Column layout for the welcome message and buttons
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),  // Add padding if you want
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "TasteBook")
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Welcome to sharing recipes")
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSignUpClick
        ) {
            Text(text = "Sign up")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onLogInClick
        ) {
            Text("Log in")
        }
    }
}
