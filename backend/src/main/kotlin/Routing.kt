package server.com

import io.ktor.server.application.*
import io.ktor.server.routing.*
import server.com.route.authRouting

fun Application.configureRouting() {
    routing {
        authRouting()
    }
}
