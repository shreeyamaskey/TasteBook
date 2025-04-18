package com.sm.tastebook.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.sm.tastebook.presentation.theme.TasteBookBackground
import com.sm.tastebook.presentation.theme.TasteBookTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun LandingScreen(
    onSignUpClick: () -> Unit,
    onLogInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TasteBookBackground {
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Green header with app title
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.primary)
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "TasteBook",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            // Spacer to push content to center
            Spacer(modifier = Modifier.weight(1f))
            
            // Green box containing all content
            Box(
                modifier = Modifier
                    .padding(horizontal = 45.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary
                        // Removed the RoundedCornerShape to make edges sharp
                    )
                    .padding(horizontal = 10.dp, vertical = 130.dp) // Increased vertical padding
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Welcome text
                    Text(
                        text = "Welcome to\nsharing recipes",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    // Sign up button
                    Button(
                        onClick = onSignUpClick,
                        modifier = Modifier
                            .width(200.dp)
                            .padding(bottom = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Sign up",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold  // Add this line to make the text bold
                            ),
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                    
                    // Divider
                    Box(
                         modifier = Modifier
                            .width(220.dp) // Make divider shorter horizontally
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f))
                            .padding(vertical = 8.dp)
                    )
                    
                    // Login section
                    Text(
                        text = "Have a profile already?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    
                    TextButton(onClick = onLogInClick) {
                        Text(
                            text = "Log in",
                            style = MaterialTheme.typography.titleMedium.copy(
                                textDecoration = TextDecoration.Underline
                            ),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            
            // Spacer to push content to center
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}