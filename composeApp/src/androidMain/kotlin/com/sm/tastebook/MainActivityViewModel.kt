package com.sm.tastebook

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sm.tastebook.data.common.datastore.UserSettings
import com.sm.tastebook.data.common.datastore.toUserAuthResultData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainActivityViewModel(
    private val dataStore: DataStore<UserSettings>
): ViewModel() {
    val authState = dataStore.data
        .map { settings -> 
            val token = settings.toUserAuthResultData().token

            if (token.isNotEmpty()) {
                try {
                    val parts = token.split(".")

                    if (parts.size >= 2) {
                        val payload = android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE)
                            .toString(Charsets.UTF_8)
                            
                        println("Debug: Current token payload: $payload")
                        println("Debug: Token generation parameters:")
                        println("Debug: - Email: ${settings.email}")
                        println("Debug: - UserID: ${settings.id}")
                    }
                } catch (e: Exception) {
                    println("Debug: Failed to decode token: ${e.message}")
                }
            }
            
            token
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )
}