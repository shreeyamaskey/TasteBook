package com.sm.tastebook.data.user

import com.sm.tastebook.data.common.util.DispatcherProvider
import com.sm.tastebook.data.common.util.Result
import com.sm.tastebook.domain.user.model.UserAuthResultData
import com.sm.tastebook.domain.user.repository.UserRepository
import kotlinx.coroutines.withContext

internal class UserRepositoryImpl(
    private val dispatcher: DispatcherProvider,
    private val authService: AuthService
) : UserRepository {
    override suspend fun signup(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        password: String
    ): Result<UserAuthResultData> {
        return withContext(dispatcher.io){
            try{
                val request = SignUpRequest(firstName, lastName, username, email, password)

                val authResponse = authService.signUp(request)

                if (authResponse.data == null){
                    Result.Error(
                        message = authResponse.errorMessage!!
                    )
                }else{
                    Result.Success(
                        data = authResponse.data.toAuthResultData()
                    )
                }
            }catch (e: Exception){
                Result.Error(
                    message = "We could not send your request :(, try later!"
                )
            }

        }
    }

    override suspend fun login(email: String, password: String): Result<UserAuthResultData> {
        return withContext(dispatcher.io){
            try{
                val request = LogInRequest(email, password)

                val authResponse = authService.logIn(request)

                if (authResponse.data == null){
                    Result.Error(
                        message = authResponse.errorMessage!!
                    )
                }else{
                    Result.Success(
                        data = authResponse.data.toAuthResultData()
                    )
                }
            }catch (e: Exception){
                Result.Error(
                    message = "We could not send your request :(, try later!"
                )
            }

        }
    }

    override suspend fun getUserProfile(userId: Int): Result<UserAuthResultData> {
        return withContext(dispatcher.io) {
            try {
                val authResponse = authService.getUserProfile(userId)
                if (authResponse.data == null) {
                    Result.Error(
                        message = authResponse.errorMessage ?: "Unknown error"
                    )
                } else {
                    Result.Success(
                        data = authResponse.data.toAuthResultData()
                    )
                }
            } catch (e: Exception) {
                Result.Error(
                    message = "We could not fetch your profile :(, try later!"
                )
            }
        }
    }
}