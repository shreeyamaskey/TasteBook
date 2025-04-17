package com.sm.tastebook.data.user

import com.sm.tastebook.data.common.KtorApi
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

internal class AuthService: KtorApi() {

    suspend fun signUp(request: SignUpRequest): AuthResponse = client.post {
        endPoint(path = "signup")
        setBody(request)
    }.body()

    suspend fun logIn(request: LogInRequest): AuthResponse = client.post {
        endPoint(path = "login")
        setBody(request)
    }.body()

}