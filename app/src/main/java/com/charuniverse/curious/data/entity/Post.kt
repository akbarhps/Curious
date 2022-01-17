package com.charuniverse.curious.data.entity

import com.charuniverse.curious.util.Preferences
import java.util.*

data class Post(
    var title: String = "",
    var content: String = "",
    var loveCount: Long = 0,
    var createdBy: String = Preferences.userId,
    var createdAt: Long = System.currentTimeMillis(),
    var id: String = UUID.randomUUID().toString(),
    var updatedAt: Long? = null,
)