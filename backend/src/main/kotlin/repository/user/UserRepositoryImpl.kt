package server.com.repository.user

import io.ktor.http.*
import server.com.dao.user.UserDao
import server.com.generateToken
import server.com.models.AuthResponse
import server.com.models.AuthResponseData
import server.com.models.LogInParams
import server.com.models.SignUpParams
import server.com.models.UpdateUserParams
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
                            email = insertedUser.email,
                            avatar = insertedUser.imageUrl,
                            token = generateToken(email = insertedUser.email, userId = insertedUser.id)
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
                            email = user.email,
                            token = generateToken(email = user.email, userId = user.id)
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

    override suspend fun updateUser(id: Int, params: UpdateUserParams): Response<AuthResponse> {
        val existingUser = userDao.findById(id)
        
        return if (existingUser == null) {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = AuthResponse(
                    errorMessage = "User not found!"
                )
            )
        } else {
            // Hash password if it's being updated
            val hashedPassword = if (params.password != null) {
                hashPassword(params.password)
            } else {
                null
            }
            
            // Check if email is being updated and if it's already in use
            if (params.email != null && params.email != existingUser.email && userAlreadyExist(params.email)) {
                return Response.Error(
                    code = HttpStatusCode.Conflict,
                    data = AuthResponse(
                        errorMessage = "A user with this email already exists!"
                    )
                )
            }
            
            val updatedUser = userDao.update(
                id = id,
                params = params.copy(
                    password = hashedPassword
                )
            )
            
            if (updatedUser == null) {
                Response.Error(
                    code = HttpStatusCode.InternalServerError,
                    data = AuthResponse(
                        errorMessage = "Sorry, your information could not be updated at this time, try later!"
                    )
                )
            } else {
                Response.Success(
                    data = AuthResponse(
                        data = AuthResponseData(
                            id = updatedUser.id,
                            firstName = updatedUser.firstName,
                            lastName = updatedUser.lastName,
                            username = updatedUser.username,
                            email = updatedUser.email,
                            avatar = updatedUser.imageUrl,
                            token = generateToken(email = updatedUser.email, userId = updatedUser.id)
                        )
                    )
                )
            }
        }
    }

    private suspend fun userAlreadyExist(email: String): Boolean {
        return userDao.findByEmail(email) != null
    }
}