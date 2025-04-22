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
            println("Debug: Full settings object: $settings")
            println("Debug: Token from settings: $token")
            println("Debug: User ID from settings: ${settings.id}")
            
            // Compare with working Postman token
            val postmanToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJ0YXN0ZWJvb2siLCJpc3MiOiJzZXJ2ZXIuY29tIiwiZW1haWwiOiJzaHJlZXlhbWFza2V5LjFAZ21haWwuY29tIiwidXNlcklkIjoxfQ.KkjO_gCviO9us8aAqth4KlqVT0I2-gIZJwgDEMoFQJE"
            
            if (token.isNotEmpty()) {
                try {
                    val parts = token.split(".")
                    val postmanParts = postmanToken.split(".")
                    
                    if (parts.size >= 2) {
                        val payload = android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE)
                            .toString(Charsets.UTF_8)
                        val postmanPayload = android.util.Base64.decode(postmanParts[1], android.util.Base64.URL_SAFE)
                            .toString(Charsets.UTF_8)
                            
                        println("Debug: Current token payload: $payload")
                        println("Debug: Postman token payload: $postmanPayload")
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