package server.com.repository.user

import io.ktor.http.cio.*
import server.com.models.AuthResponse
import server.com.models.LogInParams
import server.com.models.SignUpParams
import server.com.util.Response

interface UserRepository {
    suspend fun signUp(params: SignUpParams): Response<AuthResponse>
    suspend fun logIn(params: LogInParams): Response<AuthResponse>
}