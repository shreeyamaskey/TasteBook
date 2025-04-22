package com.sm.tastebook.domain.inventory.usecases

import com.sm.tastebook.data.common.util.Result
import com.sm.tastebook.domain.inventory.model.InventoryItemResponseData
import com.sm.tastebook.domain.inventory.repository.InventoryRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GetInventoryUseCase : KoinComponent {
    private val repository: InventoryRepository by inject()

    suspend operator fun invoke(token: String): Result<List<InventoryItemResponseData>> {
        return repository.getUserInventory(token)
    }
}