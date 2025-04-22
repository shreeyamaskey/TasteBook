package com.sm.tastebook.domain.recipe.model

import com.sm.tastebook.data.recipe.RecipeResponseData
import kotlinx.serialization.Serializable

//@Serializable
//data class RecipeResponse(
//    val data: RecipeResponseData?,
//    val errorMessage: String? = null
//)

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
    val ingredients: List<IngredientData> = emptyList(),
    val images: List<RecipeImageData> = emptyList()
)

@Serializable
data class IngredientData(
    val id: Int,
    val ingredientName: String,
    val quantity: Double,
    val measurementUnit: String
)

@Serializable
data class RecipeImageData(
    val id: Int,
    val imageUrl: String
)