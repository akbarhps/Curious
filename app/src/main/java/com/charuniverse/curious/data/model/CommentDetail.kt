package com.charuniverse.curious.data.model

import com.charuniverse.curious.data.entity.User
import com.charuniverse.curious.util.Preferences
import java.util.*

data class CommentDetail(
    var postId: String = "",
    var content: String = "",
    var createdBy: String = Preferences.userId,
    var id: String = UUID.randomUUID().toString(),
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long? = null,
    var author: User? = null,
)