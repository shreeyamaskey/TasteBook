package server.com.route

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import server.com.models.*
import server.com.repository.inventory.InventoryRepository
import server.com.util.userId

fun Routing.inventoryRouting() {
    val repository by inject<InventoryRepository>()

    authenticate {
        route("/inventory") {
            // Get all inventory items for the current user
            get {
                try {
                    val userId = call.userId()
                    val result = repository.getUserInventory(userId)
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = result
                    )
                } catch (e: Exception) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = InventoryResponse(
                            success = false,
                            errorMessage = "Failed to fetch inventory items"
                        )
                    )
                }
            }

            // Get specific inventory item
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = InventoryItemResponse(
                            success = false,
                            errorMessage = "Invalid ID format"
                        )
                    )

                val result = repository.getInventoryItem(id)
                call.respond(
                    status = if (result.success) HttpStatusCode.OK else HttpStatusCode.NotFound,
                    message = result
                )
            }

            // Add new inventory item
            post {
                try {
                    println("POST /inventory: Received request")
                    val userId = call.userId()
                    
                    val params = call.receiveNullable<AddInventoryItemParams>()
                    println("POST /inventory: Received params: $params")
                    
                    if (params == null) {
                        println("POST /inventory: Params were null")
                        return@post call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = InventoryItemResponse(
                                success = false,
                                errorMessage = "Invalid inventory data"
                            )
                        )
                    }

                    println("POST /inventory: Calling repository")
                    val result = repository.addInventoryItem(userId, params)
                    println("POST /inventory: Repository result: $result")
                    
                    call.respond(
                        status = if (result.success) HttpStatusCode.Created else HttpStatusCode.BadRequest,
                        message = result
                    )
                } catch (e: Exception) {
                    println("POST /inventory: Exception occurred: ${e.message}")
                    e.printStackTrace()
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = InventoryItemResponse(
                            success = false,
                            errorMessage = "Failed to add inventory item"
                        )
                    )
                }
            }

            // Update inventory item
            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = InventoryItemResponse(
                            success = false,
                            errorMessage = "Invalid ID format"
                        )
                    )

                val params = call.receiveNullable<UpdateInventoryItemParams>()
                    ?: return@put call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = InventoryItemResponse(
                            success = false,
                            errorMessage = "Invalid update data"
                        )
                    )

                val result = repository.updateInventoryItem(id, params)
                call.respond(
                    status = if (result.success) HttpStatusCode.OK else HttpStatusCode.NotFound,
                    message = result
                )
            }

            // Delete inventory item
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = InventoryResponse(
                            success = false,
                            errorMessage = "Invalid ID format"
                        )
                    )

                val result = repository.deleteInventoryItem(id)
                call.respond(
                    status = if (result.success) HttpStatusCode.OK else HttpStatusCode.NotFound,
                    message = result
                )
            }
        }
    }
}
