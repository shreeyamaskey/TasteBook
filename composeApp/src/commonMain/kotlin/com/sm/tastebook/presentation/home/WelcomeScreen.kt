package com.sm.tastebook.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sm.tastebook.presentation.theme.TasteBookBackground
import com.sm.tastebook.presentation.recipe.RecipeCard
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WelcomeScreen(
    onOpenRecipe: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WelcomeViewModel = koinViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        // Welcome message with user's name
        Text(
            text = "Welcome, ${uiState.userName}",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Creator's Pick section
        Text(
            text = "Creator's Pick to get you started!",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.featuredRecipe != null -> {
                RecipeCard(
                    recipe = uiState.featuredRecipe!!,
                    onSeeFullRecipe = { onOpenRecipe(uiState.featuredRecipe!!.id) },
                    onSaveRecipe = { viewModel.onSaveRecipe(uiState.featuredRecipe!!.id) }
                )
            }
            else -> {
                Text(
                    text = "No featured recipes available",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

}