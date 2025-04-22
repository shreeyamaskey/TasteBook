package com.sm.tastebook.data.inventory

import kotlinx.serialization.Serializable

@Serializable
internal data class AddInventoryItemRequest(
    val ingredientName: String,
    val quantity: Double,
    val measurementUnit: String,
    val expiryDate: Long? = null
)

@Serializable
internal data class UpdateInventoryItemRequest(
    val ingredientName: String? = null,
    val quantity: Double? = null,
    val measurementUnit: String? = null,
    val expiryDate: Long? = null
)

@Serializable
internal data class InventoryItemResponseData(
    val id: Int,
    val userId: Int,  // Add this field
    val ingredientName: String,
    val quantity: Double,
    val measurementUnit: String,
    val expiryDate: Long? = null,
    val dateAdded: Long
)

@Serializable
internal data class InventoryResponse(
    val success: Boolean = false,
    val data: List<InventoryItemResponseData>? = null,
    val errorMessage: String? = null
)

@Serializable
internal data class InventoryItemResponse(
    val success: Boolean = true,
    val data: InventoryItemResponseData? = null,
    val errorMessage: String? = null
)