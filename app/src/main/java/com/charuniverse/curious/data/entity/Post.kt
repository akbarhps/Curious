package com.charuniverse.curious.data.entity

import com.charuniverse.curious.data.model.PostDetail
import java.util.*

data class Post(
    var title: String = "",
    var content: String = "",
    var createdBy: String = "",
    var createdAt: Long = System.currentTimeMillis(),
    var id: String = "${createdBy}_${UUID.randomUUID()}",
    var updatedAt: Long? = null,
) {
    fun toFeedPost(postAuthor: User = User()): PostDetail = PostDetail(
        id = this.id,
        title = this.title,
        content = this.content,
        author = postAuthor,
        createdBy = this.createdBy,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}