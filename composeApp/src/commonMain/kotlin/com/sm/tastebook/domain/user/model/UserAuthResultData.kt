package com.sm.tastebook.domain.user.model

data class UserAuthResultData(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val avatar: String? = null,
    val token: String
)