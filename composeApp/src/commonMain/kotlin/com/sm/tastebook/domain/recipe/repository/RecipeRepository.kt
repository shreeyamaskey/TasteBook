package com.sm.tastebook.domain.recipe.repository

import com.sm.tastebook.data.common.util.Result
import com.sm.tastebook.domain.recipe.model.RecipeResponseData

interface RecipeRepository {
    // Get a specific recipe by ID
    suspend fun getRecipeById(token: String, recipeId: Int): Result<RecipeResponseData>
    
    // Get all recipes by a specific publisher
    suspend fun getRecipesByPublisher(publisherId: Int): Result<List<RecipeResponseData>>
    
    // Search for recipes
    suspend fun searchRecipes(query: String): Result<List<RecipeResponseData>>
    
    // Create a new recipe with image upload
    suspend fun createRecipe(
        token: String,
        recipeTitle: String,
        recipeDesc: String,
        preparationSteps: String,
        ingredients: List<Triple<String, Double, String>>, // (ingredientName, quantity, measurementUnit)
        mainImage: ByteArray? = null,
        additionalImages: List<ByteArray> = emptyList()
    ): Result<RecipeResponseData>
    
    // Update a recipe
    suspend fun updateRecipe(
        token: String,
        recipeId: Int,
        recipeTitle: String? = null,
        recipeDesc: String? = null,
        preparationSteps: String? = null,
        ingredients: List<Triple<String, Double, String>>? = null,
        mainImage: ByteArray? = null,
        additionalImages: List<ByteArray> = emptyList()
    ): Result<RecipeResponseData>
    
    // Delete a recipe
    suspend fun deleteRecipe(token: String, recipeId: Int): Result<Boolean>
    
    // Add an image to an existing recipe
    suspend fun addRecipeImage(
        token: String,
        recipeId: Int,
        imageData: ByteArray
    ): Result<RecipeResponseData>
    
    // Delete a recipe image
    suspend fun deleteRecipeImage(token: String, imageId: Int): Result<Boolean>
    
    // Save a recipe (increment saves count)
    suspend fun saveRecipe(token: String, recipeId: Int): Result<RecipeResponseData>
    
    // Get all recipes
    suspend fun getAllRecipes(token: String, publisherId: Int): Result<List<RecipeResponseData>>
    
    // Get saved recipes for a user
    suspend fun getSavedRecipes(token: String): Result<List<RecipeResponseData>>
}