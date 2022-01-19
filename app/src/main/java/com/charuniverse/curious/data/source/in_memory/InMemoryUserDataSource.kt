package com.charuniverse.curious.data.source.in_memory

import com.charuniverse.curious.data.model.User

object InMemoryUserDataSource {

    private var users = mutableMapOf<String, User>()

    fun getById(userId: String): User? = users[userId]

    fun add(user: User) = users.put(user.id, user)

    fun update(user: User) = users[user.id]?.let {
        it.displayName = user.displayName
        it.username = user.username
        it.updatedAt = user.updatedAt
    }

    fun clear() = users.clear()

}