package server.com

import io.ktor.server.application.*
import io.ktor.server.routing.*
import server.com.route.authRouting
import server.com.route.recipeRouting


fun Application.configureRouting() {
    routing {
        authRouting()
        recipeRouting()
    }
}
