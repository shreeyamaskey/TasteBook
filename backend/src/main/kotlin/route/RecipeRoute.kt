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
            println("Received POST /recipes request")
            var mainImageFileName = ""
            var additionalImageFileNames = mutableListOf<String>()
            var recipeParams: CreateRecipeParams? = null
            val multiPartData = call.receiveMultipart()

            println("Processing multipart data...")
            multiPartData.forEachPart { partData ->
                when(partData) {
                    is PartData.FileItem -> {
                        println("Received file part: ${partData.name}")
                        if (partData.name == "main_image") {
                            mainImageFileName = partData.saveFile(folderPath = Constants.RECIPE_IMAGES_FOLDER_PATH)
                            println("Saved main image: $mainImageFileName")
                        } else if (partData.name?.startsWith("additional_image") == true) {
                            val fileName = partData.saveFile(folderPath = Constants.RECIPE_IMAGES_FOLDER_PATH)
                            additionalImageFileNames.add(fileName)
                            println("Saved additional image: $fileName")
                        }
                    }
                    is PartData.FormItem -> {
                        println("Received form part: ${partData.name}")
                        if (partData.name == "recipe_data") {
                            println("Parsing recipe_data...")
                            recipeParams = Json.decodeFromString(partData.value)
                            println("Parsed recipe data: $recipeParams")
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

            println("Generated image URLs: mainImageUrl=$mainImageUrl, additionalImageUrls=$additionalImageUrls")

            if (recipeParams == null) {
                println("Error: Recipe data is missing")
                // Clean up any uploaded files if recipe data is missing
                if (mainImageFileName.isNotEmpty()) {
                    File("${Constants.RECIPE_IMAGES_FOLDER_PATH}/$mainImageFileName").delete()
                    println("Deleted main image file: $mainImageFileName")
                }
                additionalImageFileNames.forEach {
                    File("${Constants.RECIPE_IMAGES_FOLDER_PATH}/$it").delete()
                    println("Deleted additional image file: $it")
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
            
            println("Creating recipe in repository...")
            val result = recipeRepository.createRecipe(completeParams)
            println("Repository result: $result")
            call.respond(result.code, result.data)
        }
    }

    // Add an image to an existing recipe
    authenticate {
        post("/recipes/{id}/images") {
            println("Received POST /recipes/{id}/images request")
            val recipeId = call.parameters["id"]?.toIntOrNull()

            if (recipeId == null) {
                println("Error: Invalid recipe ID")
                call.respond(HttpStatusCode.BadRequest, "Invalid recipe ID")
                return@post
            }

            var fileName = ""
            val multiPartData = call.receiveMultipart()

            println("Processing multipart data...")
            multiPartData.forEachPart { partData ->
                when(partData) {
                    is PartData.FileItem -> {
                        println("Received file part: ${partData.name}")
                        fileName = partData.saveFile(folderPath = Constants.RECIPE_IMAGES_FOLDER_PATH)
                        println("Saved image file: $fileName")
                    }
                    else -> {}
                }
                partData.dispose()
            }

            if (fileName.isEmpty()) {
                println("Error: No image file provided")
                call.respond(HttpStatusCode.BadRequest, "No image file provided")
                return@post
            }

            val imageUrl = "${Constants.BASE_URL}${Constants.RECIPE_IMAGES_FOLDER}$fileName"
            println("Generated image URL: $imageUrl")

            println("Adding image to recipe $recipeId...")
            val result = recipeRepository.addRecipeImage(recipeId, imageUrl)
            println("Repository result: $result")
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
            // In the POST /recipes handler, replace:
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
