package com.sm.tastebook.presentation.recipe

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import org.koin.compose.viewmodel.koinViewModel
import android.widget.Toast.LENGTH_SHORT

@Composable
fun RecipeAddScreen(
    viewModel: RecipeAddViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentIngredient by viewModel.currentIngredient.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // launchers now just feed Uri back to ViewModel
    val mainImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let(viewModel::setMainImageUri)
    }
    
    val additionalImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let(viewModel::addAdditionalImageUri)
    }

    // Side effect: navigate or show error
    LaunchedEffect(uiState.isSuccess, uiState.error) {
        if (uiState.isSuccess) onNavigateBack()
        uiState.error?.let { Toast.makeText(context, it, LENGTH_SHORT).show() }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Text(
                text = "Add a Recipe!",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Title field
        item {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Title", color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)) },
                textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.secondary),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        // Description field
        item {
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Description", color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)) },
                textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.secondary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
        }
        
        // Preparation Steps field
        item {
            OutlinedTextField(
                value = uiState.preparationSteps,
                onValueChange = viewModel::onPreparationStepsChange,
                label = { Text("Preparation Steps", color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)) },
                textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.secondary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 10
            )
        }

        // Ingredients section
        item {
            Text(
                text = "Ingredients",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = currentIngredient.name,
                    onValueChange = viewModel::onIngredientNameChange,
                    label = { Text("Ingredient Name", color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)) },
                    textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = currentIngredient.amount,
                        onValueChange = viewModel::onIngredientAmountChange,
                        label = { Text("Amount", color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)) },
                        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.weight(0.6f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = currentIngredient.measurement,
                        onValueChange = viewModel::onIngredientMeasurementChange,
                        label = { Text("Measurement", color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)) },
                        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Button(
                    onClick = viewModel::addIngredient,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Ingredient")
                    Spacer(Modifier.width(4.dp))
                    Text("Add Ingredient")
                }
            }
        }

        if (uiState.ingredients.isNotEmpty()) {
            item { Text("Added Ingredients:", style = MaterialTheme.typography.titleMedium) }
            itemsIndexed(uiState.ingredients) { index, ing ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${ing.name}: ${ing.amount} ${ing.measurement}", modifier = Modifier.weight(1f))
                    IconButton(onClick = { viewModel.removeIngredient(index) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove Ingredient")
                    }
                }
                Divider()
            }
        }

        // Photos section
        item { Text("Photos", style = MaterialTheme.typography.titleLarge) }
        
        // Main image section
        item {
            Column {
                Text("Main Image (Required)", style = MaterialTheme.typography.titleMedium)
                Button(
                    onClick = { mainImageLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = "Add Main Photo")
                    Spacer(Modifier.width(8.dp))
                    Text("Select Main Photo")
                }
                
                // Display main image preview if available
                uiState.mainImageUri?.let { uri ->
                    Box(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .size(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    ) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Main Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
        
        // Additional images section
        item {
            Column {
                Text("Additional Images (Optional)", style = MaterialTheme.typography.titleMedium)
                Button(
                    onClick = { additionalImageLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = "Add Additional Photo")
                    Spacer(Modifier.width(8.dp))
                    Text("Add Additional Photo")
                }
            }
        }

        if (uiState.additionalImageUris.isNotEmpty()) {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(uiState.additionalImageUris) { idx, uri ->
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            IconButton(
                                onClick = { viewModel.removeAdditionalImageUri(idx) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(32.dp)
                                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f), 
                                               shape = RoundedCornerShape(4.dp))
                            ) {
                                Icon(Icons.Default.Delete, 
                                     contentDescription = "Remove Photo", 
                                     tint = MaterialTheme.colorScheme.onSecondary)
                            }
                        }
                    }
                }
            }
        }

        // Submit button
        item {
            Button(
                onClick = viewModel::submitRecipe,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = !uiState.isLoading && uiState.title.isNotBlank()
            ) {
                if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                else Text("Save Recipe")
            }
        }
    }
}
