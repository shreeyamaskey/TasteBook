package com.sm.tastebook.domain.inventory.model

import kotlinx.serialization.Serializable

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