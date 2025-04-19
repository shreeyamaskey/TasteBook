package server.com.route

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import server.com.models.CreateRecipeParams
import server.com.models.RecipeResponse
import server.com.models.UpdateRecipeParams
import server.com.repository.recipe.RecipeRepository
import server.com.util.Constants
import server.com.util.saveFile
import server.com.util.userId
import java.io.File

fun Routing.recipeRouting() {
    val recipeRepository by inject<RecipeRepository>()
    
    // Create a new recipe with image upload
    authenticate {
        post("/recipes") {
            var mainImageFileName = ""
            var additionalImageFileNames = mutableListOf<String>()
            var recipeParams: CreateRecipeParams? = null
            val multiPartData = call.receiveMultipart()

            multiPartData.forEachPart { partData ->
                when(partData) {
                    is PartData.FileItem -> {
                        if (partData.name == "main_image") {
                            mainImageFileName = partData.saveFile(folderPath = Constants.RECIPE_IMAGES_FOLDER_PATH)
                        } else if (partData.name?.startsWith("additional_image") == true) {
                            additionalImageFileNames.add(partData.saveFile(folderPath = Constants.RECIPE_IMAGES_FOLDER_PATH))
                        }
                    }
                    is PartData.FormItem -> {
                        if (partData.name == "recipe_data") {
                            recipeParams = Json.decodeFromString(partData.value)
                        }
                    }
                    else -> {}
                }
                partData.dispose()
            }

            val mainImageUrl = if (mainImageFileName.isNotEmpty()) {
                "${Constants.BASE_URL}${Constants.RECIPE_IMAGES_FOLDER}$mainImageFileName"
            } else null

            val additionalImageUrls = additionalImageFileNames.map { 
                "${Constants.BASE_URL}${Constants.RECIPE_IMAGES_FOLDER}$it" 
            }

            if (recipeParams == null) {
                // Clean up any uploaded files if recipe data is missing
                if (mainImageFileName.isNotEmpty()) {
                    File("${Constants.RECIPE_IMAGES_FOLDER_PATH}/$mainImageFileName").delete()
                }
                additionalImageFileNames.forEach {
                    File("${Constants.RECIPE_IMAGES_FOLDER_PATH}/$it").delete()
                }

                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = RecipeResponse(
                        errorMessage = "Could not parse recipe data"
                    )
                )
                return@post
            }

            val userId = call.userId()
            println("User ID from token: $userId")
            
            // Create a local non-nullable variable
            val nonNullParams = recipeParams!!
            
            // Set the publisherId directly
            nonNullParams.publisherId = userId
            println("Recipe params with publisher ID: $nonNullParams")
            
            // Use the params with the updated publisherId and add image URLs
            val completeParams = nonNullParams.copy(
                imageUrl = mainImageUrl,
                additionalImages = if (additionalImageUrls.isNotEmpty()) additionalImageUrls else null
            )
            println("Complete params: $completeParams")
            
            val result = recipeRepository.createRecipe(completeParams)
            println("Result from repository: $result")
            call.respond(result.code, result.data)
        }
    }

    // Add an image to an existing recipe
    authenticate {
        post("/recipes/{id}/images") {
            val recipeId = call.parameters["id"]?.toIntOrNull()

            if (recipeId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid recipe ID")
                return@post
            }

            var fileName = ""
            val multiPartData = call.receiveMultipart()

            multiPartData.forEachPart { partData ->
                when(partData) {
                    is PartData.FileItem -> {
                        fileName = partData.saveFile(folderPath = Constants.RECIPE_IMAGES_FOLDER_PATH)
                    }
                    else -> {}
                }
                partData.dispose()
            }

            if (fileName.isEmpty()) {
                call.respond(HttpStatusCode.BadRequest, "No image file provided")
                return@post
            }

            val imageUrl = "${Constants.BASE_URL}${Constants.RECIPE_IMAGES_FOLDER}$fileName"
            val result = recipeRepository.addRecipeImage(recipeId, imageUrl)
            
            call.respond(result.code, result.data)
        }
    }

    // Delete a recipe image
    authenticate {
        delete("/recipes/images/{id}") {
            val imageId = call.parameters["id"]?.toIntOrNull()

            if (imageId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid image ID")
                return@delete
            }

            val result = recipeRepository.deleteRecipeImage(imageId)
            call.respond(result.code, result.data)
        }
    }

    // Get a specific recipe by ID
    get("/recipes/{id}") {
        val recipeId = call.parameters["id"]?.toIntOrNull()

        if (recipeId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid recipe ID")
            return@get
        }

        val result = recipeRepository.getRecipeById(recipeId)
        call.respond(result.code, result.data)
    }

    // Get all recipes by a specific publisher
    get("/recipes/publisher/{id}") {
        val publisherId = call.parameters["id"]?.toIntOrNull()

        if (publisherId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid publisher ID")
            return@get
        }

        val result = recipeRepository.getRecipesByPublisher(publisherId)
        call.respond(result.code, result.data)
    }

    // Update a recipe
    authenticate {
        put("/recipes/{id}") {
            val recipeId = call.parameters["id"]?.toIntOrNull()

            if (recipeId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid recipe ID")
                return@put
            }

            var mainImageFileName = ""
            var additionalImageFileNames = mutableListOf<String>()
            var recipeParams: UpdateRecipeParams? = null
            val multiPartData = call.receiveMultipart()

            multiPartData.forEachPart { partData ->
                when(partData) {
                    is PartData.FileItem -> {
                        if (partData.name == "main_image") {
                            mainImageFileName = partData.saveFile(folderPath = Constants.RECIPE_IMAGES_FOLDER_PATH)
                        } else if (partData.name?.startsWith("additional_image") == true) {
                            additionalImageFileNames.add(partData.saveFile(folderPath = Constants.RECIPE_IMAGES_FOLDER_PATH))
                        }
                    }
                    is PartData.FormItem -> {
                        if (partData.name == "recipe_data") {
                            recipeParams = Json.decodeFromString(partData.value)
                        }
                    }
                    else -> {}
                }
                partData.dispose()
            }

            val mainImageUrl = if (mainImageFileName.isNotEmpty()) {
                "${Constants.BASE_URL}${Constants.RECIPE_IMAGES_FOLDER}$mainImageFileName"
            } else null

            val additionalImageUrls = if (additionalImageFileNames.isNotEmpty()) {
                additionalImageFileNames.map { "${Constants.BASE_URL}${Constants.RECIPE_IMAGES_FOLDER}$it" }
            } else null

            if (recipeParams == null) {
                // Clean up any uploaded files if recipe data is missing
                if (mainImageFileName.isNotEmpty()) {
                    File("${Constants.RECIPE_IMAGES_FOLDER_PATH}/$mainImageFileName").delete()
                }
                additionalImageFileNames.forEach {
                    File("${Constants.RECIPE_IMAGES_FOLDER_PATH}/$it").delete()
                }

                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = RecipeResponse(
                        errorMessage = "Could not parse recipe data"
                    )
                )
                return@put
            }

            // Add image URLs to the update params
            val completeParams = recipeParams!!.copy(
                imageUrl = mainImageUrl,
                additionalImages = additionalImageUrls
            )
            
            val result = recipeRepository.updateRecipe(recipeId, completeParams)
            call.respond(result.code, result.data)
        }
    }

    // Delete a recipe
    authenticate {
        delete("/recipes/{id}") {
            val recipeId = call.parameters["id"]?.toIntOrNull()

            if (recipeId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid recipe ID")
                return@delete
            }

            val result = recipeRepository.deleteRecipe(recipeId)
            call.respond(result.code, result.data)
        }
    }

    // Save a recipe (increment saves count)
    authenticate {
        post("/recipes/{id}/save") {
            val recipeId = call.parameters["id"]?.toIntOrNull()

            if (recipeId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid recipe ID")
                return@post
            }

            val result = recipeRepository.saveRecipe(recipeId)
            call.respond(result.code, result.data)
        }
    }

    // Search for recipes
    get("/recipes/search") {
        val query = call.request.queryParameters["q"] ?: ""

        if (query.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, "Search query cannot be empty")
            return@get
        }

        val result = recipeRepository.searchRecipes(query)
        call.respond(result.code, result.data)
    }
}
