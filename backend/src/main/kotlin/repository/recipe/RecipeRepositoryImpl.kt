package server.com.repository.recipe

import io.ktor.http.*
import server.com.dao.recipe.RecipeDao
import server.com.models.*
import server.com.util.Response

class RecipeRepositoryImpl(
    private val recipeDao: RecipeDao
) : RecipeRepository {
    
    override suspend fun createRecipe(params: CreateRecipeParams): Response<RecipeResponse> {
        val recipe = recipeDao.insert(params)
        
        return if (recipe == null) {
            Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = RecipeResponse(
                    errorMessage = "Sorry, the recipe could not be created at this time, try later!"
                )
            )
        } else {
            Response.Success(
                data = RecipeResponse(
                    data = RecipeResponseData(
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
                        }
                    )
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
                    data = RecipeResponseData(
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
                        }
                    )
                )
            )
        }
    }
    
    override suspend fun getRecipesByPublisher(publisherId: Int): Response<List<RecipeResponse>> {
        val recipes = recipeDao.findByPublisher(publisherId)
        
        return Response.Success(
            data = recipes.map { recipe ->
                RecipeResponse(
                    data = RecipeResponseData(
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
                        }
                    )
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
                        data = RecipeResponseData(
                            recipeId = updatedRecipe.recipeId,
                            publisherId = updatedRecipe.publisherId,
                            recipeTitle = updatedRecipe.recipeTitle,
                            recipeDesc = updatedRecipe.recipeDesc,
                            preparationSteps = updatedRecipe.preparationSteps,
                            publishedAt = updatedRecipe.publishedAt,
                            imageUrl = updatedRecipe.imageUrl,
                            savesCount = updatedRecipe.savesCount,
                            ingredients = updatedRecipe.ingredients.map { ingredient ->
                                IngredientResponseData(
                                    id = ingredient.id,
                                    ingredientName = ingredient.ingredientName,
                                    quantity = ingredient.quantity,
                                    measurementUnit = ingredient.measurementUnit
                                )
                            }
                        )
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
                    data = RecipeResponseData(
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
                        }
                    )
                )
            }
        )
    }
}