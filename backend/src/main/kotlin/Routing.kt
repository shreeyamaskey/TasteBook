package server.com

import io.ktor.server.application.*
import io.ktor.server.routing.*
import server.com.route.authRouting

fun Application.configureRouting() {
    routing {
        authRouting()
//        get("/") {
//            call.respondText("Hello Panda!")
//        }
//
//        get("/users") {
//            call.respond(UserService.getAllUsers())
//        }
//
//        post("/users/add") {
//            val user = call.receive<User>()
//            call.respond(UserService.createUser(user))
//        }
//
//        // Get user by ID
//        get("/users/{id}") {
//            val id = call.parameters["id"]?.toIntOrNull()
//                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
//
//            val user = UserService.getUserById(id)
//            if (user != null) {
//                call.respond(user)
//            } else {
//                call.respond(HttpStatusCode.NotFound, "User not found")
//            }
//        }
//
//        // Update user
//        put("/users/{id}") {
//            val id = call.parameters["id"]?.toIntOrNull()
//                ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
//
//            val updatedUser = call.receive<User>()
//            val result = UserService.updateUser(id, updatedUser)
//
//            if (result != null) {
//                call.respond(result)
//            } else {
//                call.respond(HttpStatusCode.NotFound, "User not found")
//            }
//        }
//
//        // Delete user
//        delete("/users/{id}") {
//            val id = call.parameters["id"]?.toIntOrNull()
//                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
//
//            if (UserService.deleteUser(id)) {
//                call.respond(HttpStatusCode.NoContent)
//            } else {
//                call.respond(HttpStatusCode.NotFound, "User not found")
//            }
//        }
    }
}
