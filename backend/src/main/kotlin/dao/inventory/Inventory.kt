package server.com.dao.inventory

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object InventoryTable : Table("inventory") {
    val id = integer("id").autoIncrement().uniqueIndex()
    val userId = integer("user_id") // relation with the user table
    val ingredientName = varchar("ingredient_name", length = 100)
    val quantity = decimal("quantity", precision = 10, scale = 2)
    val measurementUnit = varchar("measurement_unit", length = 50)
    val expiryDate = long("expiry_date").nullable() // optional expiry date as timestamp
    val dateAdded = long("date_added").default(System.currentTimeMillis())

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Inventory(
    val id: Int,
    val userId: Int,
    val ingredientName: String,
    val quantity: Double,
    val measurementUnit: String,
    val expiryDate: Long? = null,
    val dateAdded: Long
)