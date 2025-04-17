package com.sm.tastebook.data.user

import kotlinx.serialization.Serializable

@Serializable
internal data class SignUpRequest(
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val password: String
)

@Serializable
internal data class LogInRequest(
    val email: String,
    val password: String
)

@Serializable
internal data class AuthResponse(
    val data: AuthResponseData? = null,
    val errorMessage: String? = null
)

@Serializable
internal data class AuthResponseData(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val avatar: String? = null,
    val token: String
)