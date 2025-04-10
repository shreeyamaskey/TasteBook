package com.sm.tastebook.presentation.user

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sm.tastebook.presentation.theme.TasteBookBackground

@Composable
fun WelcomeScreen(
    firstName: String,
    modifier: Modifier = Modifier
) {
    TasteBookBackground {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to TasteBook,",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Hi $firstName!",
                fontSize = 24.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Start exploring recipes and sharing your culinary journey!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}