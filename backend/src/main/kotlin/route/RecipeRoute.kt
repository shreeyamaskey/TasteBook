package server.com.route
//
//import io.ktor.http.*
//import io.ktor.server.application.*
//import io.ktor.server.auth.*
//import io.ktor.server.request.*
//import io.ktor.server.response.*
//import io.ktor.server.routing.*
//import server.com.models.CreateRecipeParams
//import server.com.models.UpdateRecipeParams
//import server.com.repository.recipe.RecipeRepository
//import server.com.util.userId
//
//fun Route.recipeRoutes(recipeRepository: RecipeRepository) {
//
//    // Create a new recipe
//    authenticate {
//        post("/recipes") {
//            val params = call.receive<CreateRecipeParams>()
//            val userId = call.userId()
//
//            // Use the authenticated user's ID as the publisher ID
//            val recipeParams = params.copy(publisherId = userId)
//            val result = recipeRepository.createRecipe(recipeParams)
//
//            call.respond(result.code, result.data)
//        }
//    }
//
//    // Get a specific recipe by ID
//    get("/recipes/{id}") {
//        val recipeId = call.parameters["id"]?.toIntOrNull()
//
//        if (recipeId == null) {
//            call.respond(HttpStatusCode.BadRequest, "Invalid recipe ID")
//            return@get
//        }
//
//        val result = recipeRepository.getRecipeById(recipeId)
//        call.respond(result.code, result.data)
//    }
//
//    // Get all recipes by a specific publisher
//    get("/recipes/publisher/{id}") {
//        val publisherId = call.parameters["id"]?.toIntOrNull()
//
//        if (publisherId == null) {
//            call.respond(HttpStatusCode.BadRequest, "Invalid publisher ID")
//            return@get
//        }
//
//        val result = recipeRepository.getRecipesByPublisher(publisherId)
//        call.respond(result.code, result.data)
//    }
//
//    // Update a recipe
//    authenticate {
//        put("/recipes/{id}") {
//            val recipeId = call.parameters["id"]?.toIntOrNull()
//
//            if (recipeId == null) {
//                call.respond(HttpStatusCode.BadRequest, "Invalid recipe ID")
//                return@put
//            }
//
//            val params = call.receive<UpdateRecipeParams>()
//            val result = recipeRepository.updateRecipe(recipeId, params)
//
//            call.respond(result.code, result.data)
//        }
//    }
//
//    // Delete a recipe
//    authenticate {
//        delete("/recipes/{id}") {
//            val recipeId = call.parameters["id"]?.toIntOrNull()
//
//            if (recipeId == null) {
//                call.respond(HttpStatusCode.BadRequest, "Invalid recipe ID")
//                return@delete
//            }
//
//            val result = recipeRepository.deleteRecipe(recipeId)
//            call.respond(result.code, result.data)
//        }
//    }
//
//    // Save a recipe (increment saves count)
//    authenticate {
//        post("/recipes/{id}/save") {
//            val recipeId = call.parameters["id"]?.toIntOrNull()
//
//            if (recipeId == null) {
//                call.respond(HttpStatusCode.BadRequest, "Invalid recipe ID")
//                return@post
//            }
//
//            val result = recipeRepository.saveRecipe(recipeId)
//            call.respond(result.code, result.data)
//        }
//    }
//
//    // Search for recipes
//    get("/recipes/search") {
//        val query = call.request.queryParameters["q"] ?: ""
//
//        if (query.isBlank()) {
//            call.respond(HttpStatusCode.BadRequest, "Search query cannot be empty")
//            return@get
//        }
//
//        val result = recipeRepository.searchRecipes(query)
//        call.respond(result.code, result.data)
//    }
//}
//
//fun Application.registerRecipeRoutes(recipeRepository: RecipeRepository) {
//    routing {
//        recipeRoutes(recipeRepository)
//    }
//}