package com.sm.tastebook.data.inventory

import com.sm.tastebook.data.common.KtorApi
import io.ktor.client.call.*
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal class InventoryService : KtorApi() {


    suspend fun getUserInventory(token: String): InventoryResponse {
        val cleanToken = token.removePrefix("Bearer ").trim()
        val response = client.get {
            endPoint("inventory")
            headers {
                append(HttpHeaders.Authorization, "Bearer $cleanToken")
                append(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
            // <<< tell Ktor you want to handle errors yourself
            expectSuccess = false
        }

        val payload = response.bodyAsText().takeIf { it.isNotBlank() } ?: ""
        return if (response.status.isSuccess()) {
            // parse the 200‑OK JSON into your InventoryResponse
            try {
                Json.decodeFromString(payload)
            } catch (e: Exception) {
                // parsing failure on a 200
                InventoryResponse(success = false, errorMessage = "Malformed JSON in success response")
            }
        } else {
            // non‑2xx, payload should be something like
            // { "success": false, "data": null, "errorMessage": "Token is not valid or has expired" }
            // if it really isn’t JSON, we still wrap it:
            try {
                Json.decodeFromString<InventoryResponse>(payload)
            } catch (_: Exception) {
                InventoryResponse(success = false, errorMessage = "Unexpected ${response.status}")
            }
        }
    }


    suspend fun getInventoryItem(token: String, id: Int): InventoryItemResponse = client.get {
        endPoint(path = "inventory/$id")
        setToken(token)
        contentType(ContentType.Application.Json)
        accept(ContentType.Application.Json)
    }.body()

    suspend fun addInventoryItem(
        token: String,
        request: AddInventoryItemRequest
    ): InventoryItemResponse {
        // clean up the token
        val cleanToken = token.removePrefix("Bearer ").trim()

        // fire the POST and let us handle non-2xx ourselves
        val response = client.post {
            endPoint("inventory")
            headers {
                append(HttpHeaders.Authorization, "Bearer $cleanToken")
                append(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
            contentType(ContentType.Application.Json)
            setBody(request)
            expectSuccess = false
        }

        // grab the raw text so we can see exactly what came back
        val raw = response.bodyAsText().also {
            println("DEBUG: addInventoryItem → status=${response.status}, body=$it")
        }

        return if (response.status == HttpStatusCode.Created) {
            // happy path: server returns your InventoryItemResponse JSON
            try {
                Json.decodeFromString(raw)
            } catch (e: Exception) {
                InventoryItemResponse(success = false, errorMessage = "Malformed JSON in 201")
            }
        } else {
            // error path: server SHOULD be returning your error wrapper,
            // but if it isn’t, fall back to a generic message
            try {
                Json.decodeFromString<InventoryItemResponse>(raw)
            } catch (_: Exception) {
                InventoryItemResponse(success = false, errorMessage = "Unexpected ${response.status}")
            }
        }
    }

    suspend fun updateInventoryItem(
        token: String,
        id: Int,
        request: UpdateInventoryItemRequest
    ): InventoryItemResponse = client.put {
        endPoint(path = "inventory/$id")
        setToken(token)
        contentType(ContentType.Application.Json)
        setBody(request)
    }.body()

    suspend fun deleteInventoryItem(token: String, id: Int): InventoryResponse = client.delete {
        endPoint(path = "inventory/$id")
        setToken(token)
    }.body()
}