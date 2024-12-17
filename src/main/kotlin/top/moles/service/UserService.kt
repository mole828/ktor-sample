package top.moles.service

import top.moles.model.User
import top.moles.model.UserId

class UserService {
    operator fun get(userId: UserId): User {
        return User(
            id = userId,
            name = "User-${userId.value}",
        )
    }
}