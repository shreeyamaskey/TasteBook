package server.com.models

import kotlinx.serialization.Serializable

@Serializable
data class SignUpParams(
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class LogInParams(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val data: AuthResponseData? = null,
    val errorMessage: String? = null
)

@Serializable
data class AuthResponseData(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val username: String,
    val avatar: String? = null,
    val token: String
)