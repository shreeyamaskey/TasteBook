package server.com

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import server.com.dao.DatabaseFactory
import server.com.di.configureDI
import java.io.File

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()
    configureSerialization()
    configureDI()
    configureSecurity()
    configureRouting()
    
    // Configure static resources for serving images
    routing {
        staticFiles("/recipe_images", File("build/resources/main/static/recipe_images"))
    }
}
