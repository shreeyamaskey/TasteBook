package server.com.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateRecipeParams(
    var publisherId: Int = 0,
    val recipeTitle: String,
    val recipeDesc: String,
    val preparationSteps: String,
    val ingredients: List<CreateIngredientParams> = emptyList(),
    val imageUrl: String? = null,
    val additionalImages: List<String>? = null
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
    val ingredients: List<CreateIngredientParams>? = null,
    val additionalImages: List<String>? = null
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
    val ingredients: List<IngredientResponseData> = emptyList(),
    val images: List<RecipeImageResponseData> = emptyList()
)

@Serializable
data class IngredientResponseData(
    val id: Int,
    val ingredientName: String,
    val quantity: Double,
    val measurementUnit: String
)

@Serializable
data class RecipeImageResponseData(
    val id: Int,
    val imageUrl: String
)
