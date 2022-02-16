import io.ktor.routing.*

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*

fun Route.customerRouting() {
    route("/customer") {
        get {
            if (customerStorage.isNotEmpty()) {
                call.respond(customerStorage)
            } else {
                call.respondText("No customers found", status = HttpStatusCode.NotFound)
            }
        }
        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            val customer =
                customerStorage.find { it.id == id } ?: return@get call.respondText(
                    "No customer with id $id",
                    status = HttpStatusCode.NotFound
                )
            call.respond(customer)
        }
        post {
            val custmer = call.receive<Customer>()
            customerStorage.add(custmer)
            call.respondText("Customer stored correctly", status = HttpStatusCode.Created)

        }
        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (customerStorage.removeIf { it.id == id}) {
                call.respondText("Customer removed correctly", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not Found", status =HttpStatusCode.NotFound)
            }
        }
    }
}

fun Application.registerCustomerRoutes() {
    routing {
        customerRouting()
    }
}
