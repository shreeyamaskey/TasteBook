package com.sm.tastebook.domain.recipe.usecases

import com.sm.tastebook.data.common.util.Result
import com.sm.tastebook.domain.recipe.model.RecipeResponseData
import com.sm.tastebook.domain.recipe.repository.RecipeRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RecipeAddUseCase : KoinComponent {
    private val repository: RecipeRepository by inject()

    suspend operator fun invoke(
        token: String,
        recipeTitle: String,
        recipeDesc: String,
        preparationSteps: String,
        ingredients: List<Triple<String, Double, String>>, // (name, quantity, unit)
        mainImage: ByteArray? = null,
        additionalImages: List<ByteArray> = emptyList()
    ): Result<RecipeResponseData> {
        // Validate title
        if (recipeTitle.isBlank()) {
            return Result.Error(
                message = "Recipe title cannot be empty"
            )
        }

        // Validate description
        if (recipeDesc.isBlank()) {
            return Result.Error(
                message = "Recipe description cannot be empty"
            )
        }

        // Validate preparation steps
        if (preparationSteps.isBlank()) {
            return Result.Error(
                message = "Preparation steps cannot be empty"
            )
        }

        // Validate ingredients
        if (ingredients.isEmpty()) {
            return Result.Error(
                message = "At least one ingredient is required"
            )
        }

        // Validate main image
        if (mainImage == null) {
            return Result.Error(
                message = "A main image is required for the recipe"
            )
        }

        // All validations passed, create the recipe
        return repository.createRecipe(
            token = token,
            recipeTitle = recipeTitle,
            recipeDesc = recipeDesc,
            preparationSteps = preparationSteps,
            ingredients = ingredients,
            mainImage = mainImage,
            additionalImages = additionalImages
        )
    }
}