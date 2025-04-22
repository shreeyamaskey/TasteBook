package server.com.repository.inventory

import server.com.dao.inventory.InventoryDao
import server.com.models.*

class InventoryRepositoryImpl(
    private val dao: InventoryDao
) : InventoryRepository {

    override suspend fun addInventoryItem(userId: Int, params: AddInventoryItemParams): InventoryItemResponse {
        return try {
            println("Repository: Adding inventory item with params: $params")
            val inventory = dao.insert(userId, params)
            if (inventory != null) {
                println("Repository: Successfully added inventory item")
                InventoryItemResponse(
                    success = true,
                    data = inventory
                )
            } else {
                println("Repository: Failed to add inventory item - null response from DAO")
                InventoryItemResponse(
                    success = false,
                    errorMessage = "Failed to add inventory item"
                )
            }
        } catch (e: Exception) {
            println("Repository: Error adding inventory item: ${e.message}")
            e.printStackTrace()
            InventoryItemResponse(
                success = false,
                errorMessage = e.message ?: "An error occurred while adding inventory item"
            )
        }
    }

    override suspend fun getInventoryItem(id: Int): InventoryItemResponse {
        return try {
            val inventory = dao.findById(id)
            if (inventory != null) {
                InventoryItemResponse(
                    success = true,
                    data = inventory
                )
            } else {
                InventoryItemResponse(
                    success = false,
                    errorMessage = "Inventory item not found"
                )
            }
        } catch (e: Exception) {
            InventoryItemResponse(
                success = false,
                errorMessage = e.message ?: "An error occurred while fetching inventory item"
            )
        }
    }

    override suspend fun getUserInventory(userId: Int): InventoryResponse {
        return try {
            val inventoryItems = dao.findByUserId(userId)
            InventoryResponse(
                success = true,
                data = inventoryItems
            )
        } catch (e: Exception) {
            InventoryResponse(
                success = false,
                errorMessage = e.message ?: "An error occurred while fetching user inventory"
            )
        }
    }

    override suspend fun updateInventoryItem(id: Int, params: UpdateInventoryItemParams): InventoryItemResponse {
        return try {
            val updatedInventory = dao.update(id, params)
            if (updatedInventory != null) {
                InventoryItemResponse(
                    success = true,
                    data = updatedInventory
                )
            } else {
                InventoryItemResponse(
                    success = false,
                    errorMessage = "Failed to update inventory item"
                )
            }
        } catch (e: Exception) {
            InventoryItemResponse(
                success = false,
                errorMessage = e.message ?: "An error occurred while updating inventory item"
            )
        }
    }

    override suspend fun deleteInventoryItem(id: Int): InventoryResponse {
        return try {
            val result = dao.delete(id)
            if (result) {
                InventoryResponse(success = true)
            } else {
                InventoryResponse(
                    success = false,
                    errorMessage = "Failed to delete inventory item"
                )
            }
        } catch (e: Exception) {
            InventoryResponse(
                success = false,
                errorMessage = e.message ?: "An error occurred while deleting inventory item"
            )
        }
    }
}