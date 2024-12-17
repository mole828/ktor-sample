package top.moles.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.java.KoinJavaComponent.inject
import org.koin.ktor.ext.inject
import top.moles.model.UserId
import top.moles.model.UserSession
import top.moles.service.UserService

data class User(
    val name: String,
)

fun Application.configureRouting() {
    val userService by inject<UserService>()
    val r = routing {
        get("/") {
            call.respondText("Hello World!")
        }
        route("/sign") {
            get("/status") {
                val userSession = call.sessions.get<UserSession>()
                call.respondNullable(userSession)
            }
            get("in") {
                val user = userService[UserId("12")]
                val userSession = UserSession(userId = user.id)
                call.sessions.set(userSession)
                call.respond("OK")
            }
            get("/out") {
                call.sessions.clear<UserSession>()
                call.respond("OK")
            }
        }
        get("sessions") {
//            call.respond(sessionStorage.sessions)
        }
    }
    r.intercept(ApplicationCallPipeline.Plugins) {
        val logger = application.log
        application.log.info("${ApplicationCallPipeline.Plugins} ${call.request.uri}")
    }
    r.intercept(ApplicationCallPipeline.Call) {
        application.log.info("${ApplicationCallPipeline.Call} ${call.request.uri}")
    }
}
