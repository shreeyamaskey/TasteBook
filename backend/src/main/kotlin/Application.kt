package server.com

import io.ktor.server.application.*
import server.com.dao.DatabaseFactory
import server.com.di.configureDI

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()
    configureSerialization()
    configureDI()
    configureSecurity()
    configureRouting()
}
