package com.sm.tastebook.domain.user.usecases

import com.sm.tastebook.domain.user.model.UserAuthResultData
import com.sm.tastebook.domain.user.repository.UserRepository
import com.sm.tastebook.data.common.util.Result
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class LogInUseCase: KoinComponent {
    private val repository: UserRepository by inject()

    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<UserAuthResultData>{
        if (email.isBlank() || "@" !in email){
            return Result.Error(
                message = "Invalid email"
            )
        }
        if (password.isBlank() || password.length < 5){
            return Result.Error(
                message = "Invalid password or too short! Password has to be at least 5 characters"
            )
        }

        return repository.login(email, password)
    }
}