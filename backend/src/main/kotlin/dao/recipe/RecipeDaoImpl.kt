package server.com.dao.recipe

import org.jetbrains.exposed.sql.*
import server.com.dao.DatabaseFactory.dbQuery
import server.com.models.CreateRecipeParams
import server.com.models.UpdateRecipeParams
import java.math.BigDecimal
import java.time.ZoneOffset
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq

class RecipeDaoImpl : RecipeDao {
    override suspend fun insert(params: CreateRecipeParams): Recipe? = dbQuery {
        // Insert main recipe row
        val stmt = RecipeTable.insert {
            it[publisherId]      = params.publisherId
            it[recipeTitle]      = params.recipeTitle
            it[recipeDesc]       = params.recipeDesc
            it[preparationSteps] = params.preparationSteps
            it[imageUrl]         = params.imageUrl
        }

        // Grab the generated ID
        val newId = stmt.resultedValues
            ?.singleOrNull()
            ?.get(RecipeTable.recipeId)
            ?: return@dbQuery null

        // Insert its ingredients
        params.ingredients.forEach { ing ->
            RecipeIngredientTable.insert {
                it[recipeId]        = newId
                it[ingredientName]  = ing.ingredientName
                it[quantity]        = BigDecimal.valueOf(ing.quantity)
                it[measurementUnit] = ing.measurementUnit
            }
        }

        // Return the full recipe
        findById(newId)
    }

    override suspend fun findById(recipeId: Int): Recipe? = dbQuery {
        RecipeTable
            .select { RecipeTable.recipeId eq recipeId }
            .singleOrNull()
            ?.let { row ->
                rowToRecipe(row).copy(ingredients = loadIngredients(recipeId))
            }
    }

    override suspend fun findByPublisher(publisherId: Int): List<Recipe> = dbQuery {
        RecipeTable
            .select { RecipeTable.publisherId eq publisherId }
            .map { it[RecipeTable.recipeId] }
            .mapNotNull { findById(it) }
    }

    override suspend fun update(recipeId: Int, params: UpdateRecipeParams): Recipe? = dbQuery {
        // Update the recipe row
        RecipeTable.update({ RecipeTable.recipeId eq recipeId }) {
            params.recipeTitle?.let        { title -> it[recipeTitle]        = title }
            params.recipeDesc?.let         { desc  -> it[recipeDesc]         = desc }
            params.preparationSteps?.let   { prep  -> it[preparationSteps]  = prep }
            params.imageUrl?.let           { url   -> it[imageUrl]           = url }
        }

        // Replace ingredients if provided
        params.ingredients?.let { newList ->
            RecipeIngredientTable.deleteWhere { RecipeIngredientTable.recipeId eq recipeId }
            newList.forEach { ing ->
                RecipeIngredientTable.insert {
                    it[RecipeIngredientTable.recipeId]        = recipeId
                    it[ingredientName]  = ing.ingredientName
                    it[quantity]        = BigDecimal.valueOf(ing.quantity)
                    it[measurementUnit] = ing.measurementUnit
                }
            }
        }

        findById(recipeId)
    }

    override suspend fun delete(recipeId: Int): Boolean = dbQuery {
        RecipeTable.deleteWhere { RecipeTable.recipeId eq recipeId } > 0
    }

    override suspend fun incrementSaves(recipeId: Int): Boolean = dbQuery {
        RecipeTable.update({ RecipeTable.recipeId eq recipeId }) {
            with(SqlExpressionBuilder) {
                it.update(savesCount, savesCount + 1)
            }
        } > 0
    }

    override suspend fun search(query: String): List<Recipe> = dbQuery {
        val pat = "%${query.lowercase()}%"
        RecipeTable
            .select {
                (RecipeTable.recipeTitle.lowerCase()  like pat) or
                        (RecipeTable.recipeDesc.lowerCase()   like pat)
            }
            .map { row ->
                rowToRecipe(row).copy(ingredients = loadIngredients(row[RecipeTable.recipeId]))
            }
    }

    // ——— Helpers ———

    /** Maps the recipe row into a Recipe (without ingredients). */
    private fun rowToRecipe(row: ResultRow): Recipe = Recipe(
        recipeId        = row[RecipeTable.recipeId],
        publisherId     = row[RecipeTable.publisherId],
        recipeTitle     = row[RecipeTable.recipeTitle],
        recipeDesc      = row[RecipeTable.recipeDesc],
        preparationSteps= row[RecipeTable.preparationSteps],
        publishedAt     = row[RecipeTable.publishedAt].toEpochSecond(ZoneOffset.UTC),
        imageUrl        = row[RecipeTable.imageUrl],
        savesCount      = row[RecipeTable.savesCount],
        ingredients     = emptyList()  // filled in by caller
    )

    /** Loads all ingredients for one recipe. */
    private fun loadIngredients(recipeId: Int): List<RecipeIngredient> =
        RecipeIngredientTable
            .select { RecipeIngredientTable.recipeId eq recipeId }
            .map { row ->
                RecipeIngredient(
                    id              = row[RecipeIngredientTable.id],
                    recipeId        = row[RecipeIngredientTable.recipeId],
                    ingredientName  = row[RecipeIngredientTable.ingredientName],
                    quantity        = row[RecipeIngredientTable.quantity].toDouble(),
                    measurementUnit = row[RecipeIngredientTable.measurementUnit]
                )
            }
}
