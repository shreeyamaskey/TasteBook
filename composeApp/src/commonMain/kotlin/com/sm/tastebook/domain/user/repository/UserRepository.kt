package com.sm.tastebook.domain.user.repository

import com.sm.tastebook.domain.user.model.User

interface UserRepository {
    // Creates a new user and returns their ID
    suspend fun insertUser(user: User): Long
    
    // Finds a user by their email
    suspend fun getUserByEmail(email: String): User?
    
    // Finds a user by their username
    suspend fun getUserByUsername(username: String): User?
    
    // Checks if login credentials are valid
    suspend fun validateCredentials(username: String, password: String): User?
}