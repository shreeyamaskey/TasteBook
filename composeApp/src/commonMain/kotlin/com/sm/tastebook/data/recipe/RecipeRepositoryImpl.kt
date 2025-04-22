package com.sm.tastebook.data.recipe

import com.sm.tastebook.data.common.util.DispatcherProvider
import com.sm.tastebook.data.common.util.Result
import com.sm.tastebook.domain.recipe.model.RecipeResponseData
import com.sm.tastebook.domain.recipe.repository.RecipeRepository
import kotlinx.coroutines.withContext

// Add this import to use the mapper functions
import com.sm.tastebook.data.recipe.toDomainModel

internal class RecipeRepositoryImpl(
    private val dispatcher: DispatcherProvider,
    private val recipeService: RecipeService
) : RecipeRepository {

    override suspend fun getRecipeById(recipeId: Int): Result<RecipeResponseData> {
        return withContext(dispatcher.io) {
            try {
                val response = recipeService.getRecipeById(recipeId)
                
                if (response.data == null) {
                    Result.Error(
                        message = response.errorMessage ?: "Failed to get recipe"
                    )
                } else {
                    Result.Success(
                        data = response.data.toDomainModel()
                    )
                }
            } catch (e: Exception) {
                Result.Error(
                    message = "We could not send your request, try later!"
                )
            }
        }
    }

    override suspend fun getRecipesByPublisher(publisherId: Int): Result<List<RecipeResponseData>> {
        return withContext(dispatcher.io) {
            try {
                val recipes = recipeService.getRecipesByPublisher(publisherId)
                Result.Success(
                    data = recipes.map { it.toDomainModel() }
                )
            } catch (e: Exception) {
                Result.Error(
                    message = "We could not send your request, try later!"
                )
            }
        }
    }

    override suspend fun searchRecipes(query: String): Result<List<RecipeResponseData>> {
        return withContext(dispatcher.io) {
            try {
                val recipes = recipeService.searchRecipes(query)
                Result.Success(
                    data = recipes.map { it.toDomainModel() }
                )
            } catch (e: Exception) {
                Result.Error(
                    message = "We could not send your request, try later!"
                )
            }
        }
    }

    override suspend fun createRecipe(
        token: String,
        recipeTitle: String,
        recipeDesc: String,
        preparationSteps: String,
        ingredients: List<Triple<String, Double, String>>,
        mainImage: ByteArray?,
        additionalImages: List<ByteArray>
    ): Result<RecipeResponseData> {
        return withContext(dispatcher.io) {
            try {
                // Make sure token is properly formatted (might need "Bearer " prefix)
                val formattedToken = if (!token.startsWith("Bearer ")) "Bearer $token" else token
                
                val ingredientRequests = ingredients.map { (name, quantity, unit) ->
                    CreateIngredientRequest(
                        ingredientName = name,
                        quantity = quantity,
                        measurementUnit = unit
                    )
                }
                
                val request = CreateRecipeRequest(
                    recipeTitle = recipeTitle,
                    recipeDesc = recipeDesc,
                    preparationSteps = preparationSteps,
                    ingredients = ingredientRequests
                )
                
                // Call the service with proper error handling for the response
                try {
                    val response = recipeService.createRecipe(
                        token = formattedToken,
                        recipe = request,
                        mainImage = mainImage,
                        additionalImages = additionalImages
                    )
                    
                    if (response.data == null) {
                        Result.Error(
                            message = response.errorMessage ?: "Failed to create recipe"
                        )
                    } else {
                        Result.Success(
                            data = response.data.toDomainModel()
                        )
                    }
                } catch (e: kotlinx.serialization.SerializationException) {
                    println("Serialization error: ${e.message}")
                    Result.Error(
                        message = "Error processing server response"
                    )
                }
            } catch (e: Exception) {
                // Consider logging the actual exception for debugging
                println("Recipe creation error: ${e.message}")
                Result.Error(
                    message = "We could not send your request, try later!"
                )
            }
        }
    }

    override suspend fun updateRecipe(
        token: String,
        recipeId: Int,
        recipeTitle: String?,
        recipeDesc: String?,
        preparationSteps: String?,
        ingredients: List<Triple<String, Double, String>>?,
        mainImage: ByteArray?,
        additionalImages: List<ByteArray>
    ): Result<RecipeResponseData> {
        return withContext(dispatcher.io) {
            try {
                val ingredientRequests = ingredients?.map { (name, quantity, unit) ->
                    CreateIngredientRequest(
                        ingredientName = name,
                        quantity = quantity,
                        measurementUnit = unit
                    )
                }
                
                val request = UpdateRecipeRequest(
                    recipeTitle = recipeTitle,
                    recipeDesc = recipeDesc,
                    preparationSteps = preparationSteps,
                    ingredients = ingredientRequests
                )
                
                val response = recipeService.updateRecipe(
                    token = token,
                    recipeId = recipeId,
                    recipe = request,
                    mainImage = mainImage,
                    additionalImages = additionalImages
                )
                
                if (response.data == null) {
                    Result.Error(
                        message = response.errorMessage ?: "Failed to update recipe"
                    )
                } else {
                    Result.Success(
                        data = response.data.toDomainModel()
                    )
                }
            } catch (e: Exception) {
                Result.Error(
                    message = "We could not send your request, try later!"
                )
            }
        }
    }

    override suspend fun deleteRecipe(token: String, recipeId: Int): Result<Boolean> {
        return withContext(dispatcher.io) {
            try {
                val response = recipeService.deleteRecipe(token, recipeId)
                
                if (response.data == null) {
                    Result.Error(
                        message = response.errorMessage ?: "Failed to delete recipe"
                    )
                } else {
                    Result.Success(data = true)
                }
            } catch (e: Exception) {
                Result.Error(
                    message = "We could not send your request, try later!"
                )
            }
        }
    }

    override suspend fun addRecipeImage(
        token: String,
        recipeId: Int,
        imageData: ByteArray
    ): Result<RecipeResponseData> {
        return withContext(dispatcher.io) {
            try {
                val response = recipeService.addRecipeImage(token, recipeId, imageData)
                
                if (response.data == null) {
                    Result.Error(
                        message = response.errorMessage ?: "Failed to add image"
                    )
                } else {
                    Result.Success(
                        data = response.data.toDomainModel()
                    )
                }
            } catch (e: Exception) {
                Result.Error(
                    message = "We could not send your request, try later!"
                )
            }
        }
    }

    override suspend fun deleteRecipeImage(token: String, imageId: Int): Result<Boolean> {
        return withContext(dispatcher.io) {
            try {
                val response = recipeService.deleteRecipeImage(token, imageId)
                
                if (response.data == null) {
                    Result.Error(
                        message = response.errorMessage ?: "Failed to delete image"
                    )
                } else {
                    Result.Success(data = true)
                }
            } catch (e: Exception) {
                Result.Error(
                    message = "We could not send your request, try later!"
                )
            }
        }
    }

    override suspend fun saveRecipe(token: String, recipeId: Int): Result<RecipeResponseData> {
        return withContext(dispatcher.io) {
            try {
                val response = recipeService.saveRecipe(token, recipeId)
                
                if (response.data == null) {
                    Result.Error(
                        message = response.errorMessage ?: "Failed to save recipe"
                    )
                } else {
                    Result.Success(
                        data = response.data.toDomainModel()
                    )
                }
            } catch (e: Exception) {
                Result.Error(
                    message = "We could not send your request, try later!"
                )
            }
        }
    }

    override suspend fun getAllRecipes(token: String, publisherId: Int): Result<List<RecipeResponseData>> {
        return withContext(dispatcher.io) {
            try {
                println("Debug: Making API call to get recipes")
                val response = recipeService.getAllRecipes(token, publisherId)
                println("Debug: API Response: $response")

                if (response.isNotEmpty()) {
                    println("Debug: Success, recipes count: ${response.size}")
                    Result.Success(
                        data = response.mapNotNull { wrapper -> 
                            wrapper.data?.toDomainModel() // Added safe call operator
                        }
                    )
                } else {
                    println("Debug: No recipes found")
                    Result.Success(emptyList())
                }
            } catch (e: Exception) {
                println("Debug: Exception in getAllRecipes: ${e.message}")
                e.printStackTrace()
                Result.Error(
                    message = "Could not send request, try later!"
                )
            }
        }
    }

    override suspend fun getSavedRecipes(token: String): Result<List<RecipeResponseData>> {
        return withContext(dispatcher.io) {
            try {
                val response = recipeService.getSavedRecipes(token)
                Result.Success(
                    data = response.mapNotNull { wrapper -> 
                        wrapper.data?.toDomainModel()
                    }
                )
            } catch (e: Exception) {
                Result.Error(
                    message = "We could not send your request, try later!"
                )
            }
        }
    }
}