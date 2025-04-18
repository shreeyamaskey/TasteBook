package com.sm.tastebook

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import com.sm.tastebook.data.common.datastore.UserSettings
import com.sm.tastebook.data.common.datastore.toUserAuthResultData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainActivityViewModel(
    private val dataStore: DataStore<UserSettings>
): ViewModel() {
    val authState = dataStore.data.map { it.toUserAuthResultData().token }
}