package com.sm.tastebook.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun TasteBookFooter(
    currentRoute: String,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // Determine which screens should show the footer
    val shouldShowFooter = currentRoute in listOf("home", "profile", "recipe_view", "recipe_add", "inventory", "saved_recipes")
    
    if (shouldShowFooter) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Saved recipes button
                IconButton(
                    onClick = { 
                        if (currentRoute != "saved_recipes") {
                            navController.navigate("saved_recipes")
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Saved Recipes",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                
                // Add recipe button (center)
                IconButton(
                    onClick = { 
                        if (currentRoute != "recipe_add") {
                            navController.navigate("recipe_add")
                        }
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Recipe",
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                // Profile button
                IconButton(
                    onClick = { 
                        if (currentRoute != "profile") {
                            navController.navigate("profile")
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}