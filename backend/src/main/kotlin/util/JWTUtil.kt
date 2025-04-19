package server.com.util

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

/**
 * Extension function to get the authenticated user's ID from the JWT token
 */
fun ApplicationCall.userId(): Int {
    val principal = this.principal<JWTPrincipal>()
    return principal?.payload?.getClaim("userId")?.asInt() ?: -1
}