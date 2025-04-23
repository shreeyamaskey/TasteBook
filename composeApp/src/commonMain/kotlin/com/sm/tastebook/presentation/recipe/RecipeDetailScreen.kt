package com.sm.tastebook.presentation.recipe

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RecipeDetailScreen(
    recipeId: Int,
    viewModel: RecipeDetailViewModel = koinViewModel()
) {
    // Call loadRecipe when the screen is first composed
    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            // Recipe Title
            Text(
                text = uiState.title,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        item {
            // Publisher Info and Date
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "By ${uiState.publisherName}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "• ${uiState.publishDate}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Main Image
        item {
            AsyncImage(
                model = uiState.imageUrl?.replace("localhost", "146.86.116.124"),
                contentDescription = uiState.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp)
            )
        }

        // Description
        item {
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = uiState.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Ingredients
        item {
            Text(
                text = "Ingredients",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(uiState.ingredients) { ingredient ->
            Text(
                text = "• $ingredient",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
            )
        }

        // Preparation Steps
        item {
            Text(
                text = "Preparation Steps",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            Text(
                text = uiState.preparationSteps,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}