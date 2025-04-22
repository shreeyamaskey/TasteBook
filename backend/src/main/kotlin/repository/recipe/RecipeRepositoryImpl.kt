package server.com.repository.recipe

import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import server.com.dao.recipe.RecipeDao
import server.com.models.*
import server.com.util.Response

class RecipeRepositoryImpl(
    private val recipeDao: RecipeDao
) : RecipeRepository {
    
    override suspend fun createRecipe(params: CreateRecipeParams): Response<RecipeResponse> {
        return try {
            println("Repository: Creating recipe with params: $params")
            println("Repository: Image URL in params: ${params.imageUrl}")
            println("Repository: Additional images in params: ${params.additionalImages}")
            
            val recipe = recipeDao.insert(params)
            
            if (recipe != null) {
                println("Repository: Recipe created successfully: $recipe")
                println("Repository: Recipe ID: ${recipe.recipeId}")
                println("Repository: Recipe ingredients count: ${recipe.ingredients.size}")
                println("Repository: Recipe images count: ${recipe.images.size}")
                
                Response.Success(
                    data = RecipeResponse(
                        data = mapRecipeToResponseData(recipe)
                    )
                )
            } else {
                println("Repository: Recipe creation failed - DAO returned null")
                Response.Error(
                    code = HttpStatusCode.InternalServerError,
                    data = RecipeResponse(
                        errorMessage = "Sorry, the recipe could not be created at this time, try later!"
                    )
                )
            }
        } catch (e: Exception) {
            println("Repository ERROR: Exception during recipe creation: ${e.message}")
            e.printStackTrace()
            Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = RecipeResponse(
                    errorMessage = "Sorry, the recipe could not be created at this time, try later!"
                )
            )
        }
    }
    
    override suspend fun getRecipeById(recipeId: Int): Response<RecipeResponse> {
        val recipe = recipeDao.findById(recipeId)
        
        return if (recipe == null) {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = RecipeResponse(
                    errorMessage = "Recipe not found!"
                )
            )
        } else {
            Response.Success(
                data = RecipeResponse(
                    data = mapRecipeToResponseData(recipe)
                )
            )
        }
    }
    
    override suspend fun getRecipesByPublisher(publisherId: Int): Response<List<RecipeResponse>> {
        val recipes = recipeDao.findByPublisher(publisherId)
        
        return Response.Success(
            data = recipes.map { recipe ->
                RecipeResponse(
                    data = mapRecipeToResponseData(recipe)
                )
            }
        )
    }
    
    override suspend fun updateRecipe(recipeId: Int, params: UpdateRecipeParams): Response<RecipeResponse> {
        val existingRecipe = recipeDao.findById(recipeId)
        
        return if (existingRecipe == null) {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = RecipeResponse(
                    errorMessage = "Recipe not found!"
                )
            )
        } else {
            val updatedRecipe = recipeDao.update(recipeId, params)
            
            if (updatedRecipe == null) {
                Response.Error(
                    code = HttpStatusCode.InternalServerError,
                    data = RecipeResponse(
                        errorMessage = "Sorry, the recipe could not be updated at this time, try later!"
                    )
                )
            } else {
                Response.Success(
                    data = RecipeResponse(
                        data = mapRecipeToResponseData(updatedRecipe)
                    )
                )
            }
        }
    }
    
    override suspend fun deleteRecipe(recipeId: Int): Response<Boolean> {
        val existingRecipe = recipeDao.findById(recipeId)
        
        return if (existingRecipe == null) {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = false
            )
        } else {
            val deleted = recipeDao.delete(recipeId)
            
            if (deleted) {
                Response.Success(data = true)
            } else {
                Response.Error(
                    code = HttpStatusCode.InternalServerError,
                    data = false
                )
            }
        }
    }
    
    override suspend fun saveRecipe(recipeId: Int): Response<Boolean> {
        val existingRecipe = recipeDao.findById(recipeId)
        
        return if (existingRecipe == null) {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = false
            )
        } else {
            val saved = recipeDao.incrementSaves(recipeId)
            
            if (saved) {
                Response.Success(data = true)
            } else {
                Response.Error(
                    code = HttpStatusCode.InternalServerError,
                    data = false
                )
            }
        }
    }
    
    override suspend fun searchRecipes(query: String): Response<List<RecipeResponse>> {
        val recipes = recipeDao.search(query)
        
        return Response.Success(
            data = recipes.map { recipe ->
                RecipeResponse(
                    data = mapRecipeToResponseData(recipe)
                )
            }
        )
    }
    
    override suspend fun addRecipeImage(recipeId: Int, imageUrl: String): Response<RecipeResponse> {
        val existingRecipe = recipeDao.findById(recipeId)
        
        return if (existingRecipe == null) {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = RecipeResponse(
                    errorMessage = "Recipe not found!"
                )
            )
        } else {
            val addedImage = recipeDao.addRecipeImage(recipeId, imageUrl)
            
            if (addedImage == null) {
                Response.Error(
                    code = HttpStatusCode.InternalServerError,
                    data = RecipeResponse(
                        errorMessage = "Sorry, the image could not be added at this time, try later!"
                    )
                )
            } else {
                // Fetch the updated recipe with the new image
                val updatedRecipe = recipeDao.findById(recipeId)
                
                if (updatedRecipe == null) {
                    Response.Error(
                        code = HttpStatusCode.InternalServerError,
                        data = RecipeResponse(
                            errorMessage = "Error retrieving updated recipe!"
                        )
                    )
                } else {
                    Response.Success(
                        data = RecipeResponse(
                            data = mapRecipeToResponseData(updatedRecipe)
                        )
                    )
                }
            }
        }
    }
    
    override suspend fun deleteRecipeImage(imageId: Int): Response<Boolean> {
        val deleted = recipeDao.deleteRecipeImage(imageId)
        
        return if (deleted) {
            Response.Success(data = true)
        } else {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = false
            )
        }
    }
    
    // Helper function to map Recipe to RecipeResponseData
    private fun mapRecipeToResponseData(recipe: server.com.dao.recipe.Recipe): RecipeResponseData {
        return RecipeResponseData(
            recipeId = recipe.recipeId,
            publisherId = recipe.publisherId,
            recipeTitle = recipe.recipeTitle,
            recipeDesc = recipe.recipeDesc,
            preparationSteps = recipe.preparationSteps,
            publishedAt = recipe.publishedAt,
            imageUrl = recipe.imageUrl,
            savesCount = recipe.savesCount,
            ingredients = recipe.ingredients.map { ingredient ->
                IngredientResponseData(
                    id = ingredient.id,
                    ingredientName = ingredient.ingredientName,
                    quantity = ingredient.quantity,
                    measurementUnit = ingredient.measurementUnit
                )
            },
            images = recipe.images.map { image ->
                RecipeImageResponseData(
                    id = image.id,
                    imageUrl = image.imageUrl
                )
            }
        )
    }
}