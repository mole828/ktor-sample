package top.moles.model

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class UserId(val value: String)

@Serializable
data class UserSession(val userId: UserId)

@Serializable
data class User (
    val id: UserId,
    val name: String,
)