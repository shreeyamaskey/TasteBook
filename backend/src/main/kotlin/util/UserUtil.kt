package server.com.util

import io.ktor.server.application.*

fun ApplicationCall.getCurrentUserId(): Int {
    return this.userId()
}