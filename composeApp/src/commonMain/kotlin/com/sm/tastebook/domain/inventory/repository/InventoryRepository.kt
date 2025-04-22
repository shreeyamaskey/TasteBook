package com.sm.tastebook.domain.inventory.repository

import com.sm.tastebook.data.common.util.Result
import com.sm.tastebook.domain.inventory.model.InventoryItemResponseData

internal interface InventoryRepository {
    suspend fun getUserInventory(token: String): Result<List<InventoryItemResponseData>>
    suspend fun getInventoryItem(token: String, id: Int): Result<InventoryItemResponseData>
    suspend fun addInventoryItem(
        token: String,
        name: String,
        quantity: Double,
        unit: String,
        expiryDate: Long?
    ): Result<InventoryItemResponseData>
    suspend fun updateInventoryItem(
        token: String,
        id: Int,
        name: String?,
        quantity: Double?,
        unit: String?,
        expiryDate: Long?
    ): Result<InventoryItemResponseData>
    suspend fun deleteInventoryItem(token: String, id: Int): Result<Boolean>
}