package com.sm.tastebook.data.inventory

import com.sm.tastebook.domain.inventory.model.InventoryItemResponseData as DomainInventoryItemResponseData

internal fun InventoryItemResponseData.toDomainModel(): DomainInventoryItemResponseData {
    return DomainInventoryItemResponseData(
        id = id,
        userId = userId,
        ingredientName = ingredientName,
        quantity = quantity,
        measurementUnit = measurementUnit,
        expiryDate = expiryDate,
        dateAdded = dateAdded
    )
}