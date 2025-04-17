package server.com.repository.recipe

import server.com.models.CreateRecipeParams
import server.com.models.RecipeResponse
import server.com.models.UpdateRecipeParams
import server.com.util.Response

interface RecipeRepository {
    suspend fun createRecipe(params: CreateRecipeParams): Response<RecipeResponse>
    suspend fun getRecipeById(recipeId: Int): Response<RecipeResponse>
    suspend fun getRecipesByPublisher(publisherId: Int): Response<List<RecipeResponse>>
    suspend fun updateRecipe(recipeId: Int, params: UpdateRecipeParams): Response<RecipeResponse>
    suspend fun deleteRecipe(recipeId: Int): Response<Boolean>
    suspend fun saveRecipe(recipeId: Int): Response<Boolean>
    suspend fun searchRecipes(query: String): Response<List<RecipeResponse>>
}