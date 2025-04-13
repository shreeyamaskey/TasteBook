package server.com.repository.user

import io.ktor.http.*
import server.com.dao.user.UserDao
import server.com.generateToken
import server.com.models.AuthResponse
import server.com.models.AuthResponseData
import server.com.models.LogInParams
import server.com.models.SignUpParams
import server.com.security.hashPassword
import server.com.util.Response

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {
    override suspend fun signUp(params: SignUpParams): Response<AuthResponse> {
        return if (userAlreadyExist(params.email)) {
            Response.Error(
                code = HttpStatusCode.Conflict,
                data = AuthResponse(
                    errorMessage = "A user with this email already exists!"
                )
            )
        }
        else{
            val insertedUser = userDao.insert(params)

            if (insertedUser == null) {
                Response.Error(
                    code = HttpStatusCode.InternalServerError,
                    data = AuthResponse(
                        errorMessage = "Sorry, the user could not be registered at this time, try later!"
                    )
                )
            } else {
                Response.Success(
                    data = AuthResponse(
                        data = AuthResponseData(
                            id = insertedUser.id,
                            firstName = insertedUser.firstName,
                            lastName = insertedUser.lastName,
                            username = insertedUser.username,
                            avatar = insertedUser.imageUrl,
                            token = generateToken(params.email)
                        )
                    )
                )
            }
        }
    }

    override suspend fun logIn(params: LogInParams): Response<AuthResponse> {
        val user = userDao.findByEmail(params.email)

        return if (user == null) {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = AuthResponse(
                    errorMessage = "Invalid, no user found with this email. Try again!"
                )
            )
        } else {
            val hashedPassword = hashPassword(params.password)

            if (user.password == hashedPassword) {
                Response.Success(
                    data = AuthResponse(
                        data = AuthResponseData(
                            id = user.id,
                            firstName = user.firstName,
                            lastName = user.lastName,
                            username = user.username,
                            token = generateToken(params.email)
                        )
                    )
                )
            } else {
                Response.Error(
                    code = HttpStatusCode.Forbidden,
                    data = AuthResponse(
                        errorMessage = "Invalid, wrong password!"
                    )
                )
            }
        }
    }

    private suspend fun userAlreadyExist(email: String): Boolean {
        return userDao.findByEmail(email) != null
    }
}