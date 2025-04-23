package com.sm.tastebook.data.recipe

import com.sm.tastebook.data.common.KtorApi
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.accept
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.utils.io.core.ByteReadPacket
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


internal class RecipeService : KtorApi() {

    // Get a specific recipe by ID
    suspend fun getRecipeById(token: String, recipeId: Int): RecipeResponse = client.get {
        endPoint(path = "recipes/$recipeId")
        // Format token properly
        val formattedToken = if (!token.startsWith("Bearer ")) "Bearer $token" else token
        setToken(formattedToken)
    }.body()

    // Get all recipes by a specific publisher
    suspend fun getRecipesByPublisher(publisherId: Int): List<RecipeResponseData> = client.get {
        endPoint(path = "recipes/publisher/$publisherId")
    }.body()

    // Search for recipes
    suspend fun searchRecipes(query: String): List<RecipeResponseData> = client.get {
        endPoint(path = "recipes/search?q=$query")
    }.body()

    // Create a new recipe with image upload
    suspend fun createRecipe(
        token: String,
        recipe: CreateRecipeRequest,
        mainImage: ByteArray? = null,
        additionalImages: List<ByteArray> = emptyList()
    ): RecipeResponse {
        val cleanToken = token.removePrefix("Bearer ").trim()

        // 1) Build the multipart payload
        val multipartBody = MultiPartFormDataContent(formData {
            append("recipe_data", Json.encodeToString(recipe))

            mainImage?.let { bytes ->
                append(
                    key = "main_image",
                    value = ByteReadPacket(bytes),
                    headers = Headers.build {
                        append(
                            HttpHeaders.ContentDisposition,
                            """form-data; name="main_image"; filename="main.jpg""""
                        )
                        append(HttpHeaders.ContentType, "image/jpeg")
                    }
                )
            }

            additionalImages.forEachIndexed { idx, bytes ->
                append(
                    key = "additional_image_$idx",
                    value = ByteReadPacket(bytes),
                    headers = Headers.build {
                        append(
                            HttpHeaders.ContentDisposition,
                            """form-data; name="additional_image_$idx"; filename="extra_$idx.jpg""""
                        )
                        append(HttpHeaders.ContentType, "image/jpeg")
                    }
                )
            }
        })

        // 2) Fire the request with your multipart client
        val response = createMultipartClient().post {
            endPoint("recipes")
            setToken(cleanToken)

            // *** This line is critical: ***
//            contentType(ContentType.MultiPart.FormData)

            setBody(multipartBody)
            accept(ContentType.Application.Json)
            expectSuccess = false
        }

        // 3) Read the raw text (empty if blank)
        val payload = response.bodyAsText().ifBlank { "" }
        println("createRecipe raw status=${response.status}, body=$payload")

        // 4) Decode depending on 2xx vs. non‑2xx
        return if (response.status.isSuccess()) {
            // success – try to parse into your normal success envelope
            runCatching { Json.decodeFromString<RecipeResponse>(payload) }
                .getOrElse { RecipeResponse(null, "Invalid JSON in success response") }
        } else {
            // failure – server should still reply with your RecipeResponse(errorMessage=…)
            runCatching { Json.decodeFromString<RecipeResponse>(payload) }
                .getOrElse { RecipeResponse(null, "Unexpected ${response.status}") }
        }
    }

    // Update a recipe
    suspend fun updateRecipe(
        token: String,
        recipeId: Int,
        recipe: UpdateRecipeRequest,
        mainImage: ByteArray? = null,
        additionalImages: List<ByteArray> = emptyList()
    ): RecipeResponse {
        val multipartClient = createMultipartClient()
        
        return multipartClient.put {
            endPoint(path = "recipes/$recipeId")
            setToken(token)
            
            setBody(MultiPartFormDataContent(formData {
                append("recipe_data", Json.encodeToString(recipe))
                
                mainImage?.let { bytes ->
                    append(
                        key = "main_image",
                        value = ByteReadPacket(bytes),
                        headers = Headers.build {
                            append(HttpHeaders.ContentDisposition, """form-data; name="main_image"; filename="main.jpg"""")
                            append(HttpHeaders.ContentType, "image/jpeg")
                        }
                    )
                }

                additionalImages.forEachIndexed { idx, bytes ->
                    append(
                        key = "additional_image_$idx",
                        value = ByteReadPacket(bytes),
                        headers = Headers.build {
                            append(HttpHeaders.ContentDisposition, """form-data; name="additional_image_$idx"; filename="extra_$idx.jpg"""")
                            append(HttpHeaders.ContentType, "image/jpeg")
                        }
                    )
                }
            }))
        }.body()
    }

    // Delete a recipe
    suspend fun deleteRecipe(token: String, recipeId: Int): RecipeResponse = client.delete {
        endPoint(path = "recipes/$recipeId")
        setToken(token)
    }.body()

    // Add an image to an existing recipe
    suspend fun addRecipeImage(
        token: String,
        recipeId: Int,
        imageData: ByteArray
    ): RecipeResponse {
        // Create a separate client for multipart requests
        val multipartClient = createMultipartClient()
        
        return multipartClient.post {
            endPoint(path = "recipes/$recipeId/images")
            setToken(token)
//            setupMultipartRequest()
            
            setBody(MultiPartFormDataContent(formData {
                append(
                    key = "image",
                    value = ByteReadPacket(imageData),
                    headers = Headers.build {
                        append(HttpHeaders.ContentDisposition, """form-data; name="image"; filename="recipe_image.jpg"""")
                        append(HttpHeaders.ContentType, "image/jpeg")
                    }
                )
            }))
        }.body()
    }

    // Delete a recipe image
    suspend fun deleteRecipeImage(token: String, imageId: Int): RecipeResponse = client.delete {
        endPoint(path = "recipes/images/$imageId")
        setToken(token)
    }.body()

    // Save a recipe (increment saves count)
    suspend fun saveRecipe(token: String, recipeId: Int): RecipeResponse = client.post {
        endPoint(path = "recipes/$recipeId/save")
        setToken(token)
    }.body()


    suspend fun getAllRecipes(token: String, publisherId: Int): List<RecipeResponse> = client.get {
        endPoint(path = "recipes/publisher/$publisherId")
        setToken(token)
        contentType(ContentType.Application.Json)
        accept(ContentType.Application.Json)
    }.body()

    
    // Get saved recipes for a user
    suspend fun getSavedRecipes(token: String): List<RecipeResponse> = client.get {
        endPoint(path = "recipes/saved")
        setToken(token)
    }.body()
}
