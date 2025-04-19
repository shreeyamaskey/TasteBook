package server.com.dao.recipe

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import server.com.dao.user.UserTable

object RecipeTable: Table(name = "recipe"){
    val recipeId = integer(name = "recipe_id").autoIncrement().uniqueIndex()
    val publisherId = integer(name = "publisher_id").references(ref = UserTable.id, onDelete = ReferenceOption.CASCADE)
    val recipeTitle = varchar(name = "recipe_title", length = 250)
    val recipeDesc = text("recipe_desc")
    val preparationSteps = text("preparation_steps")
    val publishedAt = datetime(name = "published_at").defaultExpression(defaultValue = CurrentDateTime)
    val imageUrl = text(name = "image_url").nullable()
    val savesCount = integer(name = "saves_count").default(defaultValue = 0)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(recipeId)
}

object RecipeImageTable : Table("recipe_images") {
    val id = integer("id").autoIncrement()
    val recipeId = integer("recipe_id").references(RecipeTable.recipeId, onDelete = ReferenceOption.CASCADE)
    val imageUrl = text("image_url")
    
    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}

object RecipeIngredientTable : Table("recipe_ingredients") {
    val id = integer("id").autoIncrement()
    val recipeId = integer("recipe_id").references(RecipeTable.recipeId, onDelete = ReferenceOption.CASCADE)
    val ingredientName = varchar("ingredient_name", length = 100)
    val quantity = decimal("quantity", precision = 10, scale = 2)
    val measurementUnit = varchar("measurement_unit", length = 50)
    
    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}

@Serializable
data class RecipeImage(
    val id: Int,
    val recipeId: Int,
    val imageUrl: String
)

@Serializable
data class RecipeIngredient(
    val id: Int,
    val recipeId: Int,
    val ingredientName: String,
    val quantity: Double,
    val measurementUnit: String
)

@Serializable
data class Recipe(
    val recipeId: Int,
    val publisherId: Int,
    val recipeTitle: String,
    val recipeDesc: String,
    val preparationSteps: String,
    val publishedAt: Long,
    val imageUrl: String? = null,
    val savesCount: Int = 0,
    val ingredients: List<RecipeIngredient> = emptyList(),
    val images: List<RecipeImage> = emptyList()
)
