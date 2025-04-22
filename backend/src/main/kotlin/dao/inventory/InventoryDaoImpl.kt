package server.com.dao.inventory

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import server.com.dao.DatabaseFactory.dbQuery
import server.com.models.*
import java.math.BigDecimal

class InventoryDaoImpl : InventoryDao {
    override suspend fun insert(userId: Int, params: AddInventoryItemParams): InventoryItemResponseData? = dbQuery {
        try {
            val insertStatement = InventoryTable.insert {
                it[InventoryTable.userId] = userId
                it[ingredientName] = params.ingredientName
                it[quantity] = BigDecimal.valueOf(params.quantity)
                it[measurementUnit] = params.measurementUnit
                it[expiryDate] = params.expiryDate
            }

            insertStatement.resultedValues?.singleOrNull()?.let { row ->
                rowToInventoryItem(row)
            }
        } catch (e: Exception) {
            println("Error inserting inventory item: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    override suspend fun findById(id: Int): InventoryItemResponseData? = dbQuery {
        InventoryTable
            .select { InventoryTable.id eq id }
            .map { rowToInventoryItem(it) }
            .singleOrNull()
    }

    override suspend fun findByUserId(userId: Int): List<InventoryItemResponseData> = dbQuery {
        InventoryTable
            .select { InventoryTable.userId eq userId }
            .map { rowToInventoryItem(it) }
    }

    override suspend fun update(id: Int, params: UpdateInventoryItemParams): InventoryItemResponseData? = dbQuery {
        InventoryTable.update({ InventoryTable.id eq id }) {
            params.ingredientName?.let { name -> it[ingredientName] = name }
            params.quantity?.let { qty -> it[quantity] = BigDecimal.valueOf(qty) }
            params.measurementUnit?.let { unit -> it[measurementUnit] = unit }
            params.expiryDate?.let { date -> it[expiryDate] = date }
        }

        findById(id)
    }

    override suspend fun delete( id: Int): Boolean = dbQuery {
        try {
            InventoryTable.deleteWhere { InventoryTable.id eq id } > 0
        } catch (e: Exception) {
            println("Error deleting inventory item: ${e.message}")
            false
        }
    }
}

private fun rowToInventoryItem(row: ResultRow): InventoryItemResponseData {
        return InventoryItemResponseData(
            id = row[InventoryTable.id],
            userId = row[InventoryTable.userId],  // Add this field
            ingredientName = row[InventoryTable.ingredientName],
            quantity = row[InventoryTable.quantity].toDouble(),
            measurementUnit = row[InventoryTable.measurementUnit],
            expiryDate = row[InventoryTable.expiryDate],
            dateAdded = row[InventoryTable.dateAdded]
        )
    }