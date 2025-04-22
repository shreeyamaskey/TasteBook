package com.sm.tastebook.data.recipe

import com.sm.tastebook.domain.recipe.model.IngredientData
import com.sm.tastebook.domain.recipe.model.RecipeImageData
//import com.sm.tastebook.domain.recipe.model.RecipeResponse as DomainRecipeResponse
import com.sm.tastebook.domain.recipe.model.RecipeResponseData as DomainRecipeResponseData


internal fun RecipeResponseData.toDomainModel(): DomainRecipeResponseData {
    return DomainRecipeResponseData(
        recipeId = this.recipeId,
        publisherId = this.publisherId,
        recipeTitle = this.recipeTitle,
        recipeDesc = this.recipeDesc,
        preparationSteps = this.preparationSteps,
        publishedAt = this.publishedAt,
        imageUrl = this.imageUrl,
        savesCount = this.savesCount,
        ingredients = this.ingredients.map { it.toDomainModel() },
        images = this.images.map { it.toDomainModel() }
    )
}
//
//internal fun RecipeResponse.toDomainModel(): DomainRecipeResponse {
//    return DomainRecipeResponse(
//        data = this.data,
//        errorMessage = this.errorMessage
//    )
//}


internal fun IngredientResponseData.toDomainModel(): IngredientData {
    return IngredientData(
        id = this.id,
        ingredientName = this.ingredientName,
        quantity = this.quantity,
        measurementUnit = this.measurementUnit
    )
}

internal fun RecipeImageResponseData.toDomainModel(): RecipeImageData {
    return RecipeImageData(
        id = this.id,
        imageUrl = this.imageUrl
    )
}
