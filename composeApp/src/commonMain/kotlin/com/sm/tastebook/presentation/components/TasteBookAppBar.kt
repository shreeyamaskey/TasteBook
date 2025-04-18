package com.sm.tastebook.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import org.jetbrains.compose.resources.painterResource
import tastebook.composeapp.generated.resources.Res
import tastebook.composeapp.generated.resources.grocery_icon


@Composable
fun TasteBookAppBar(
    currentRoute: String,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // Determine which type of AppBar to show based on the route
    val isSimpleAppBar = currentRoute in listOf("landing", "login", "signup")
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp)
    ) {
        if (isSimpleAppBar) {
            // Simple AppBar with centered TasteBook title
            Text(
                text = "TasteBook",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            // Full AppBar with TasteBook on left and icons on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // TasteBook title on the left
                Text(
                    text = "TasteBook",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                
                // Navigation icons on the right
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Home icon
                    IconButton(
                        onClick = { 
                            if (currentRoute != "home") {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    
                    IconButton(
                        onClick = { 
                            if (currentRoute != "inventory") {
                                navController.navigate("inventory")
                            }
                        }
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.grocery_icon),
                            contentDescription = "Inventory",
                            modifier = Modifier.size(24.dp),
                            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                                MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                    
                }
            }
        }
    }
}