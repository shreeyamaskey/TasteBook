package server.com.repository.inventory

import server.com.models.*

interface InventoryRepository {
    suspend fun addInventoryItem(userId: Int, params: AddInventoryItemParams): InventoryItemResponse
    suspend fun getInventoryItem(id: Int): InventoryItemResponse
    suspend fun getUserInventory(userId: Int): InventoryResponse
    suspend fun updateInventoryItem(id: Int, params: UpdateInventoryItemParams): InventoryItemResponse
    suspend fun deleteInventoryItem(id: Int): InventoryResponse
}