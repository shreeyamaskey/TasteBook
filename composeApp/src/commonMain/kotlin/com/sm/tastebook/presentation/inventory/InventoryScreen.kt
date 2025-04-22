package com.sm.tastebook.presentation.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.material3.TextFieldDefaults
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel = koinViewModel()
) {
    // Replace collectAsStateWithLifecycle with regular state access
    val uiState = viewModel.uiState
    var showAddDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Inventory Management",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Search and Sort Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                placeholder = { Text("Search ingredients") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier.weight(1f),
                singleLine = true,
//                colors = TextFieldDefaults.outlinedTextFieldColors(
//                    focusedBorderColor = MaterialTheme.colorScheme.primary,
//                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
//                )
            )
            
            // Sort Button
            IconButton(onClick = { /* Sort functionality to be implemented */ }) {
                Icon(
                    Icons.Default.Sort,
                    contentDescription = "Sort",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            // Add Button
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add")
            }
        }
        
        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Ingredient",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(2f)
            )
            Text(
                text = "Quantity",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Unit",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(48.dp)) // Space for actions
        }
        
        // Inventory Items List
        if (uiState.items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Your inventory is empty. Add some ingredients!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(uiState.filteredItems) { item ->
                    InventoryItemRow(
                        item = item,
                        onDelete = { viewModel.deleteItem(item.id) }
                    )
                    Divider()
                }
            }
        }
    }
    
    // Add Item Dialog
    if (showAddDialog) {
        AddInventoryItemDialog(
            onDismiss = { showAddDialog = false },
            onAddItem = { name, quantity, unit, expiryDate ->
                viewModel.addItem(name, quantity, unit, expiryDate)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun InventoryItemRow(
    item: InventoryItemUiModel,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.ingredientName,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(2f)
            )
            Text(
                text = item.quantity.toString(),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = item.measurementUnit,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Added: ${formatDate(item.dateAdded)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            item.expiryDate?.let {
                Text(
                    text = "Expires: ${formatDate(it)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return format.format(date)
}

@Composable
fun AddInventoryItemDialog(
    onDismiss: () -> Unit,
    onAddItem: (name: String, quantity: Double, unit: String, expiryDate: Long?) -> Unit
) {
    var ingredientName by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("") }
    var measurementUnit by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf<Long?>(null) }
    var quantityError by remember { mutableStateOf<String?>(null) }
    
    fun validateQuantity(text: String) {
        quantityError = when {
            text.isBlank() -> "Quantity is required"
            text.toDoubleOrNull() == null -> "Please enter a valid number"
            text.toDoubleOrNull()!! <= 0 -> "Quantity must be greater than 0"
            else -> null
        }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Add Inventory Item",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Ingredient Name
                OutlinedTextField(
                    value = ingredientName,
                    onValueChange = { ingredientName = it },
                    label = { Text("Ingredient Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Quantity
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { 
                        quantityText = it
                        validateQuantity(it)
                    },
                    label = { Text("Quantity") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = quantityError != null,
                    supportingText = quantityError?.let { { Text(it) } }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Measurement Unit
                OutlinedTextField(
                    value = measurementUnit,
                    onValueChange = { measurementUnit = it },
                    label = { Text("Measurement Unit") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Expiry Date (you might want to add a date picker here)
                // For now, let's use a button to show we need to implement it
                Button(
                    onClick = { /* TODO: Implement date picker */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (expiryDate != null) "Change Expiry Date" else "Add Expiry Date (Optional)")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Add Button
                Button(
                    onClick = {
                        val quantity = quantityText.toDoubleOrNull() ?: 0.0
                        onAddItem(ingredientName, quantity, measurementUnit, expiryDate)
                    },
                    enabled = ingredientName.isNotBlank() && 
                             quantityError == null && 
                             quantityText.isNotBlank() && 
                             measurementUnit.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Item")
                }
            }
        }
    }
}