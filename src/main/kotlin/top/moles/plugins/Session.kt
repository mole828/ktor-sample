package top.moles.plugins

import io.github.crackthecodeabhi.kreds.args.SetOption
import io.github.crackthecodeabhi.kreds.connection.KredsClient
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject
import top.moles.model.UserSession
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class MySessionStorageMem : SessionStorage {
    val sessions = mutableMapOf<String, String>()
    override suspend fun invalidate(id: String) {
        sessions.remove(id)
    }

    override suspend fun read(id: String): String {
        return sessions[id] ?: throw NoSuchElementException("Session $id not found")
    }

    override suspend fun write(id: String, value: String) {
        sessions[id] = value
    }
}
class RedisSessionStorage(
    private val kredsClient: KredsClient,
    private val timeToLive: Duration = 30.minutes
): SessionStorage {
    private fun makeKey(id: String) = "ktor:session:$id"

    override suspend fun invalidate(id: String) {
        kredsClient.del(makeKey(id))
    }

    override suspend fun read(id: String): String {
        return kredsClient.get(makeKey(id)) ?: throw NoSuchElementException("Session $id not found")
    }

    private val setOption = SetOption.Builder().apply {
        exSeconds = timeToLive.inWholeSeconds.toULong()
    }.build()

    override suspend fun write(id: String, value: String) {
        kredsClient.set(makeKey(id), value, setOption = setOption)
    }
}

fun Application.configureSession() {
    val kredsClient by inject<KredsClient>()
    install(Sessions) {
        cookie<UserSession>("user_session", RedisSessionStorage(kredsClient))
    }
}