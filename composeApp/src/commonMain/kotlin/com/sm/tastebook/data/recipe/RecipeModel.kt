package com.sm.tastebook.data.recipe

import kotlinx.serialization.Serializable

@Serializable
internal data class CreateRecipeRequest(
    // var publisherId: Int? = null,
    val recipeTitle: String,
    val recipeDesc: String,
    val preparationSteps: String,
    val ingredients: List<CreateIngredientRequest> = emptyList(),
    // val imageUrl: String? = null,
    // val additionalImages: List<String>? = null
)

@Serializable
internal data class CreateIngredientRequest(
    val ingredientName: String,
    val quantity: Double,
    val measurementUnit: String
)

@Serializable
internal data class UpdateRecipeRequest(
    val recipeTitle: String? = null,
    val recipeDesc: String? = null,
    val preparationSteps: String? = null,
    val imageUrl: String? = null,
    val ingredients: List<CreateIngredientRequest>? = null,
    val additionalImages: List<String>? = null
)

@Serializable
internal data class RecipeResponse(
    val data: RecipeResponseData? = null,
    val errorMessage: String? = null
)

@Serializable
internal data class RecipeResponseData(
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
internal data class IngredientResponseData(
    val id: Int,
    val ingredientName: String,
    val quantity: Double,
    val measurementUnit: String
)

@Serializable
internal data class RecipeImageResponseData(
    val id: Int,
    val imageUrl: String
)
