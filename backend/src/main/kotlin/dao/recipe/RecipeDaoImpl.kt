package server.com.dao.recipe

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import server.com.dao.DatabaseFactory.dbQuery
import server.com.models.CreateRecipeParams
import server.com.models.UpdateRecipeParams
import java.math.BigDecimal
import java.time.ZoneOffset
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq

class RecipeDaoImpl : RecipeDao {
    override suspend fun insert(params: CreateRecipeParams): Recipe? = dbQuery {
        // 1) Insert the recipe row and grab the generated ID row immediately
        val insertStmt = RecipeTable.insert {
            it[publisherId]      = params.publisherId
            it[recipeTitle]      = params.recipeTitle
            it[recipeDesc]       = params.recipeDesc
            it[preparationSteps] = params.preparationSteps
            it[imageUrl]         = params.imageUrl
        }

        val row = insertStmt.resultedValues
            ?.singleOrNull()
            ?: return@dbQuery null

        val newId = row[RecipeTable.recipeId]
        println("Successfully inserted recipe with ID: $newId")

        // 2) Insert ingredients in the *same* transaction
        params.ingredients.forEach { ing ->
            RecipeIngredientTable.insert {
                it[recipeId]        = newId
                it[ingredientName]  = ing.ingredientName
                it[quantity]        = BigDecimal.valueOf(ing.quantity)
                it[measurementUnit] = ing.measurementUnit
            }
        }

        // 3) Insert images in the same transaction
        params.imageUrl?.let { mainUrl ->
            RecipeImageTable.insert {
                it[RecipeImageTable.recipeId] = newId
                it[RecipeImageTable.imageUrl] = mainUrl
            }
        }
        params.additionalImages?.forEach { url ->
            RecipeImageTable.insert {
                it[RecipeImageTable.recipeId] = newId
                it[RecipeImageTable.imageUrl] = url
            }
        }

        // 4) Now map the inserted row to your domain object,
        //    and load ingredients & images—all inside this transaction.
        val ingredients = loadIngredients(newId)
        val images      = loadImages(newId)

        val inserted = rowToRecipe(row)
            .copy(ingredients = ingredients, images = images)
        println("Mapped inserted recipe: $inserted")
        inserted
    }

    override suspend fun findById(recipeId: Int): Recipe? = dbQuery {
        println("Attempting to find recipe with ID: $recipeId")
        RecipeTable
            .select { RecipeTable.recipeId eq recipeId }
            .singleOrNull()
            ?.let {
                rowToRecipe(it).copy(
                    ingredients = loadIngredients(recipeId),
                    images      = loadImages(recipeId)
                )
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
        
        // Replace additional images if provided
        params.additionalImages?.let { newImages ->
            RecipeImageTable.deleteWhere { RecipeImageTable.recipeId eq recipeId }
            newImages.forEach { imageUrl ->
                RecipeImageTable.insert {
                    it[RecipeImageTable.recipeId] = recipeId
                    it[RecipeImageTable.imageUrl] = imageUrl
                }
            }
        }

        findById(recipeId)
    }

    override suspend fun delete(recipeId: Int): Boolean = dbQuery {
        // The foreign key constraints will automatically delete related ingredients and images
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
                val id = row[RecipeTable.recipeId]
                rowToRecipe(row).copy(
                    ingredients = loadIngredients(id),
                    images = loadImages(id)
                )
            }
    }
    
    // New methods for handling recipe images
    override suspend fun addRecipeImage(recipeId: Int, imageUrl: String): RecipeImage? = dbQuery {
        val stmt = RecipeImageTable.insert {
            it[RecipeImageTable.recipeId] = recipeId
            it[RecipeImageTable.imageUrl] = imageUrl
        }
        
        stmt.resultedValues?.singleOrNull()?.let { row ->
            RecipeImage(
                id = row[RecipeImageTable.id],
                recipeId = row[RecipeImageTable.recipeId],
                imageUrl = row[RecipeImageTable.imageUrl]
            )
        }
    }
    
    override suspend fun getRecipeImages(recipeId: Int): List<RecipeImage> = dbQuery {
        loadImages(recipeId)
    }
    
    override suspend fun deleteRecipeImage(imageId: Int): Boolean = dbQuery {
        RecipeImageTable.deleteWhere { RecipeImageTable.id eq imageId } > 0
    }

    // ——— Helpers ———

    /** Maps the recipe row into a Recipe (without ingredients or images). */
    private fun rowToRecipe(row: ResultRow): Recipe = Recipe(
        recipeId        = row[RecipeTable.recipeId],
        publisherId     = row[RecipeTable.publisherId],
        recipeTitle     = row[RecipeTable.recipeTitle],
        recipeDesc      = row[RecipeTable.recipeDesc],
        preparationSteps= row[RecipeTable.preparationSteps],
        publishedAt     = row[RecipeTable.publishedAt].toEpochSecond(ZoneOffset.UTC),
        imageUrl        = row[RecipeTable.imageUrl],
        savesCount      = row[RecipeTable.savesCount],
        ingredients     = emptyList(),  // filled in by caller
        images          = emptyList()   // filled in by caller
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
            
    /** Loads all images for one recipe. */
    private fun loadImages(recipeId: Int): List<RecipeImage> =
        RecipeImageTable
            .select { RecipeImageTable.recipeId eq recipeId }
            .map { row ->
                RecipeImage(
                    id       = row[RecipeImageTable.id],
                    recipeId = row[RecipeImageTable.recipeId],
                    imageUrl = row[RecipeImageTable.imageUrl]
                )
            }
}

