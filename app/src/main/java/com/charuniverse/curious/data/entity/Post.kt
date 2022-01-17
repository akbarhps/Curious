package com.charuniverse.curious.data.entity

import com.charuniverse.curious.util.Preferences
import java.util.*

data class Post(
    var title: String = "",
    var content: String = "",
    var createdBy: String = Preferences.userId,
    var createdAt: Long = System.currentTimeMillis(),
    var id: String = "${createdAt}_${UUID.randomUUID()}",
    var updatedAt: Long? = null,
) {
}