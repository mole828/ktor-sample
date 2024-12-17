package top.moles.plugins

import io.github.crackthecodeabhi.kreds.connection.Endpoint
import io.github.crackthecodeabhi.kreds.connection.KredsClient
import io.github.crackthecodeabhi.kreds.connection.KredsClientConfig
import io.github.crackthecodeabhi.kreds.connection.newClient
import io.ktor.server.application.*
import io.ktor.server.application.*
import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import top.moles.model.UserId
import top.moles.service.UserService

fun Application.configureService() {
    runBlocking {
        install(Koin) {
            slf4jLogger()
            modules(module {
                singleOf(::UserService)
                single<KredsClient> {
                    runBlocking {
                        val client = newClient(Endpoint.from("localhost:6379"))
                        client.auth("app")
                        client
                    }
                }
            })
        }
        val userService by inject<UserService>()
        log.info("UserService: ${userService.get(userId = UserId("12"))}")
        val kredsClient by inject<KredsClient>()
        kredsClient.incr("ktor_init_times")
    }
}