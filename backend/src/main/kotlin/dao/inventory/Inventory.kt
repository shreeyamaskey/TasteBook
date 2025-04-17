package server.com.dao.inventory

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object InventoryTable : Table("inventory") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id") // assuming a relation with the user table
    val ingredientName = varchar("ingredient_name", length = 100)
    val quantity = decimal("quantity", precision = 10, scale = 2)
    val measurementUnit = varchar("measurement_unit", length = 50)
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Inventory(
    val id: Int
)