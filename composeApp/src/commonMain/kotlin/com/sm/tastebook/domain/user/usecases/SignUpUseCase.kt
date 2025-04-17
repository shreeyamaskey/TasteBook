package com.sm.tastebook.domain.user.usecases

import com.sm.tastebook.domain.user.model.UserAuthResultData
import com.sm.tastebook.domain.user.repository.UserRepository
import com.sm.tastebook.data.common.util.Result
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SignUpUseCase: KoinComponent {
    private val repository: UserRepository by inject()

    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        password: String
    ): Result<UserAuthResultData>{
        if (firstName.isBlank() || firstName.length < 3){
            return Result.Error(
                message = "Invalid first name. This cannot be blank or less than three characters"
            )
        }
        if (lastName.isBlank() || lastName.length <= 3){
            return Result.Error(
                message = "Invalid last name. This cannot be blank or less than three characters"
            )
        }
        if (username.isBlank() || username.length <= 3){
            return Result.Error(
                message = "Invalid username. This cannot be blank or less than three characters"
            )
        }
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

        return repository.signup(firstName, lastName, username, email, password)
    }
}