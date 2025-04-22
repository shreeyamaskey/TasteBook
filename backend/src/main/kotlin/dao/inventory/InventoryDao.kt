package server.com.dao.inventory

import server.com.models.AddInventoryItemParams
import server.com.models.UpdateInventoryItemParams
import server.com.models.InventoryItemResponseData

interface InventoryDao {
    suspend fun insert(userId: Int, params: AddInventoryItemParams): InventoryItemResponseData?
    suspend fun findById(id: Int): InventoryItemResponseData?
    suspend fun findByUserId(userId: Int): List<InventoryItemResponseData>
    suspend fun update(id: Int, params: UpdateInventoryItemParams): InventoryItemResponseData?
    suspend fun delete(id: Int): Boolean
}