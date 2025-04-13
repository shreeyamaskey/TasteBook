package server.com.route

import io.ktor.http.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import io.ktor.server.request.*
import io.ktor.server.response.*
import server.com.models.AuthResponse
import server.com.models.LogInParams
import server.com.models.SignUpParams
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
}