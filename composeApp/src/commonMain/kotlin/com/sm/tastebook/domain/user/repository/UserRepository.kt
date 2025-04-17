package com.sm.tastebook.domain.user.repository

import com.sm.tastebook.domain.user.model.UserAuthResultData
import com.sm.tastebook.data.common.util.Result

internal interface UserRepository {

    // Inserts the user and returns a result sealed class
    suspend fun signup(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        password: String
    ): Result<UserAuthResultData>
    
    // Log in
    suspend fun login(
        email: String,
        password: String
    ): Result<UserAuthResultData>

}