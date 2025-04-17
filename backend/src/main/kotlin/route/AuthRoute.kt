package server.com.route

import io.ktor.http.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import io.ktor.server.request.*
import io.ktor.server.response.*
import server.com.dao.user.User
import server.com.models.AuthResponse
import server.com.models.LogInParams
import server.com.models.SignUpParams
import server.com.models.UpdateUserParams
import server.com.repository.user.UserRepository

fun Routing.authRouting() {
    val repository by inject<UserRepository>()

    route(path = "/signup") {
        post {

            val params = call.receiveNullable<SignUpParams>()

            if (params == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = AuthResponse(
                        errorMessage = "Invalid credentials!"
                    )
                )

                return@post
            }

            val result = repository.signUp(params = params)
            call.respond(
                status = result.code,
                message = result.data
            )

        }
    }

    route(path = "/login") {
        post {

            val params = call.receiveNullable<LogInParams>()

            if (params == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = AuthResponse(
                        errorMessage = "Invalid credentials!"
                    )
                )

                return@post
            }

            val result = repository.logIn(params = params)
            call.respond(
                status = result.code,
                message = result.data
            )

        }
    }

    route(path = "/update_user/{id}") {
        put {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@put call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = AuthResponse(
                        errorMessage = "Invalid ID format"
                    )
                )

            val params = call.receiveNullable<UpdateUserParams>()
                ?: return@put call.respond(
                    HttpStatusCode.BadRequest,
                    AuthResponse(errorMessage = "Invalid user data")
                )

            val result = repository.updateUser(id, params)
            call.respond(
                status = result.code,
                message = result.data
            )
        }
    }
}