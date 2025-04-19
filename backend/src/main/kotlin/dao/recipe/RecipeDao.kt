package server.com.dao.recipe

import server.com.models.CreateRecipeParams
import server.com.models.UpdateRecipeParams

interface RecipeDao {
    suspend fun insert(params: CreateRecipeParams): Recipe?
    suspend fun findById(recipeId: Int): Recipe?
    suspend fun findByPublisher(publisherId: Int): List<Recipe>
    suspend fun update(recipeId: Int, params: UpdateRecipeParams): Recipe?
    suspend fun delete(recipeId: Int): Boolean
    suspend fun incrementSaves(recipeId: Int): Boolean
    suspend fun search(query: String): List<Recipe>
    suspend fun addRecipeImage(recipeId: Int, imageUrl: String): RecipeImage?
    suspend fun getRecipeImages(recipeId: Int): List<RecipeImage>
    suspend fun deleteRecipeImage(imageId: Int): Boolean
}