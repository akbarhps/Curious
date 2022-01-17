package com.charuniverse.curious.data.model

import com.charuniverse.curious.data.entity.User
import java.util.*

data class PostDetail(
    var id: String = UUID.randomUUID().toString(),
    var title: String = "",
    var content: String = "",
    var loveCount: Long = 0,
    var commentCount: Long = 0,
    var createdBy: String = "",
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long? = null,
    var author: User? = null,
    var comments: HashMap<String, CommentDetail>? = null,
    var lovers: HashMap<String, Long>? = null,
    var isViewerLoved: Boolean = false,
)