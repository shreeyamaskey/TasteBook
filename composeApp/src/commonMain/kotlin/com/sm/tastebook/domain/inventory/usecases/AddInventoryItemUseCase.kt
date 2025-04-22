package com.sm.tastebook.domain.inventory.usecases

import com.sm.tastebook.data.common.util.Result
import com.sm.tastebook.domain.inventory.model.InventoryItemResponseData
import com.sm.tastebook.domain.inventory.repository.InventoryRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AddInventoryItemUseCase : KoinComponent {
    private val repository: InventoryRepository by inject()

    suspend operator fun invoke(
        token: String,
        name: String,
        quantity: Double,
        unit: String,
        expiryDate: Long? = null
    ): Result<InventoryItemResponseData> {
        if (name.isBlank()) {
            return Result.Error(message = "Ingredient name is required")
        }
        if (quantity <= 0) {
            return Result.Error(message = "Quantity must be greater than 0")
        }
        if (unit.isBlank()) {
            return Result.Error(message = "Measurement unit is required")
        }

        return repository.addInventoryItem(token, name, quantity, unit, expiryDate)
    }
}