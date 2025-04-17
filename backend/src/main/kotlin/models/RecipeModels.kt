package server.com.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateRecipeParams(
    val publisherId: Int,
    val recipeTitle: String,
    val recipeDesc: String,
    val preparationSteps: String,
    val imageUrl: String? = null,
    val ingredients: List<CreateIngredientParams> = emptyList()
)

@Serializable
data class CreateIngredientParams(
    val ingredientName: String,
    val quantity: Double,
    val measurementUnit: String
)

@Serializable
data class UpdateRecipeParams(
    val recipeTitle: String? = null,
    val recipeDesc: String? = null,
    val preparationSteps: String? = null,
    val imageUrl: String? = null,
    val ingredients: List<CreateIngredientParams>? = null
)

@Serializable
data class RecipeResponse(
    val data: RecipeResponseData? = null,
    val errorMessage: String? = null
)

@Serializable
data class RecipeResponseData(
    val recipeId: Int,
    val publisherId: Int,
    val recipeTitle: String,
    val recipeDesc: String,
    val preparationSteps: String,
    val publishedAt: Long,
    val imageUrl: String? = null,
    val savesCount: Int = 0,
    val ingredients: List<IngredientResponseData> = emptyList()
)

@Serializable
data class IngredientResponseData(
    val id: Int,
    val ingredientName: String,
    val quantity: Double,
    val measurementUnit: String
)
