package com.charuniverse.curious.util

import com.charuniverse.curious.data.entity.User

/**
 * cache very repetitive data to load faster
 */
object Cache {

    var users = mutableMapOf<String, User>()

    fun clearCache() {
        users = mutableMapOf()
    }

}