package com.sm.tastebook.data.inventory

import com.sm.tastebook.data.common.util.DispatcherProvider
import com.sm.tastebook.data.common.util.Result
import com.sm.tastebook.domain.inventory.model.InventoryItemResponseData
import com.sm.tastebook.domain.inventory.repository.InventoryRepository
import kotlinx.coroutines.withContext

internal class InventoryRepositoryImpl(
    private val dispatcher: DispatcherProvider,
    private val service: InventoryService
) : InventoryRepository {

    override suspend fun getUserInventory(token: String): Result<List<InventoryItemResponseData>> {
        return withContext(dispatcher.io) {
            try {
                println("Debug: Making API call to get inventory") // Debug log
                val response = service.getUserInventory(token)
                println("Debug: API Response: $response") // Debug log

                if (response.success && response.data != null) {
                    println("Debug: Success, items count: ${response.data.size}") // Debug log
                    Result.Success(data = response.data.map { it.toDomainModel() })
                } else {
                    println("Debug: Error: ${response.errorMessage}") // Debug log
                    Result.Error(message = response.errorMessage ?: "Failed to fetch inventory")
                }
            } catch (e: Exception) {
                println("Debug: Exception in getUserInventory: ${e.message}") // Debug log
                e.printStackTrace()
                Result.Error(message = "Could not send request, try later!")
            }
        }
    }


    override suspend fun getInventoryItem(token: String, id: Int): Result<InventoryItemResponseData> {
        return withContext(dispatcher.io) {
            try {
                val response = service.getInventoryItem(token, id)
                if (response.data == null) {
                    Result.Error(message = response.errorMessage ?: "Failed to fetch item")
                } else {
                    Result.Success(data = response.data.toDomainModel())
                }
            } catch (e: Exception) {
                Result.Error(message = "Could not send request, try later!")
            }
        }
    }

    override suspend fun addInventoryItem(
        token: String,
        name: String,
        quantity: Double,
        unit: String,
        expiryDate: Long?
    ): Result<InventoryItemResponseData> {
        return withContext(dispatcher.io) {
            try {
                val request = AddInventoryItemRequest(
                    ingredientName = name,
                    quantity = quantity,
                    measurementUnit = unit,
                    expiryDate = expiryDate
                )
                val response = service.addInventoryItem(token, request)
                if (response.data == null) {
                    Result.Error(message = response.errorMessage ?: "Failed to add item")
                } else {
                    Result.Success(data = response.data.toDomainModel())
                }
            } catch (e: Exception) {
                Result.Error(message = "Could not send request, try later!")
            }
        }
    }

    override suspend fun updateInventoryItem(
        token: String,
        id: Int,
        name: String?,
        quantity: Double?,
        unit: String?,
        expiryDate: Long?
    ): Result<InventoryItemResponseData> {
        return withContext(dispatcher.io) {
            try {
                val request = UpdateInventoryItemRequest(
                    ingredientName = name,
                    quantity = quantity,
                    measurementUnit = unit,
                    expiryDate = expiryDate
                )
                val response = service.updateInventoryItem(token, id, request)
                if (response.data == null) {
                    Result.Error(message = response.errorMessage ?: "Failed to update item")
                } else {
                    Result.Success(data = response.data.toDomainModel())
                }
            } catch (e: Exception) {
                Result.Error(message = "Could not send request, try later!")
            }
        }
    }

    override suspend fun deleteInventoryItem(token: String, id: Int): Result<Boolean> {
        return withContext(dispatcher.io) {
            try {
                val response = service.deleteInventoryItem(token, id)
                if (!response.success) {
                    Result.Error(message = response.errorMessage ?: "Failed to delete item")
                } else {
                    Result.Success(data = true)
                }
            } catch (e: Exception) {
                Result.Error(message = "Could not send request, try later!")
            }
        }
    }
}