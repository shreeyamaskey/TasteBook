package com.sm.tastebook.data.common

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.logging.*



private const val BASE_URL = "http://146.86.116.124:8080/"

internal abstract class KtorApi {

    // -------------- DEFAULT CLIENT WITH LOGGING --------------
    protected val client = HttpClient(CIO) {
        install(Logging) {
            logger = Logger.SIMPLE
            level  = LogLevel.HEADERS
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys    = true
                prettyPrint          = true
                isLenient            = true
                encodeDefaults       = true
                coerceInputValues    = true
            })
        }
    }

    fun HttpRequestBuilder.endPoint(path: String) {
        url {
            takeFrom(BASE_URL)
            path(path)
        }
        headers {
            append(HttpHeaders.Accept, ContentType.Application.Json.toString())
            append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
    }

    fun HttpRequestBuilder.setToken(token: String) {
        headers {
            append(name = "Authorization", value = "Bearer $token")
        }
    }


    // -------------- MULTIPART CLIENT WITH LOGGING --------------
    fun createMultipartClient(): HttpClient = HttpClient(CIO) {
        install(Logging) {
            logger = Logger.SIMPLE
            level  = LogLevel.HEADERS
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint       = true
                isLenient         = true
            })
        }
        engine {
            pipelining = true
        }
    }
}