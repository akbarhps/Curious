package com.charuniverse.curious.util

import com.charuniverse.curious.data.entity.User

/**
 * firebase database doesn't have join feature, so
 * cache very repetitive data to load faster
 */
object Cache {

    var users = mutableMapOf<String, User>()

    fun clearCache() {
        users = mutableMapOf()
    }

}