package server.com

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.*

fun Application.configureSerialization() {
    install(ContentNegotiation){
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
}
