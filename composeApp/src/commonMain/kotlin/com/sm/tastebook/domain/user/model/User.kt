package com.sm.tastebook.domain.user.model

data class User(
    val id: Long? = null,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val password: String,
    val createdAt: Long
)