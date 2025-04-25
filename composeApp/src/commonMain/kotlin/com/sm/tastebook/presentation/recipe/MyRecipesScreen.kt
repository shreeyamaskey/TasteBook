package com.sm.tastebook.presentation.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import tastebook.composeapp.generated.resources.Res
import tastebook.composeapp.generated.resources.banner
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@Composable
fun MyRecipesScreen(
    onOpenRecipe: (Int) -> Unit,
    viewModel: MyRecipesViewModel = koinViewModel()
) {
    val ui by viewModel.state.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "My Recipes",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            IconButton(onClick = { viewModel.refreshRecipes() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh recipes",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        when {
            ui.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            ui.recipes.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nothing posted yet. Post to get started!",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center)
                }
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(ui.recipes) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onSeeFullRecipe = { onOpenRecipe(recipe.id) },
                            onSaveRecipe = { viewModel.onSaveRecipe(recipe.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeCard(
    recipe: RecipeUiModel,
    onSeeFullRecipe: () -> Unit,
    onSaveRecipe: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Gray)
    ) {
        Column(Modifier.fillMaxWidth()) {
            // Recipe Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            ) {
                println("Debug: Loading image from URL: ${recipe.imageUrl}")  // Add this debug line
                AsyncImage(
                    model = recipe.imageUrl?.replace("localhost", "146.86.116.124"),  // Temporary fix
                    contentDescription = recipe.title,
                    placeholder = painterResource(Res.drawable.banner),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    error = painterResource(Res.drawable.banner)
                )
            }

            // Recipe Title with better formatting
            Text(
                text = recipe.title,  // Remove the "Title: " prefix
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.secondary.copy()
            )

            // Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSeeFullRecipe,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("See full recipe")
                }

                Button(
                    onClick = onSaveRecipe,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Save recipe")
                }
            }
        }
    }
}
