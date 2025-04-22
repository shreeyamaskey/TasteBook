package server.com.models

import kotlinx.serialization.Serializable

@Serializable
data class AddInventoryItemParams(
    val ingredientName: String,
    val quantity: Double,
    val measurementUnit: String,
    val expiryDate: Long? = null
)

@Serializable
data class UpdateInventoryItemParams(
    val ingredientName: String? = null,
    val quantity: Double? = null,
    val measurementUnit: String? = null,
    val expiryDate: Long? = null
)

@Serializable
data class InventoryItemResponseData(
    val id: Int,
    val userId: Int,  // Add this field
    val ingredientName: String,
    val quantity: Double,
    val measurementUnit: String,
    val expiryDate: Long? = null,
    val dateAdded: Long
)

@Serializable
data class InventoryResponse(
    val success: Boolean = true,
    val data: List<InventoryItemResponseData>? = null,
    val errorMessage: String? = null
)

@Serializable
data class InventoryItemResponse(
    val success: Boolean = true,
    val data: InventoryItemResponseData? = null,
    val errorMessage: String? = null
)