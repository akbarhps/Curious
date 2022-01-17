package com.charuniverse.curious.data.model

import com.charuniverse.curious.data.entity.Post
import com.charuniverse.curious.data.entity.User
import java.util.*

data class PostDetail(
    var id: String = UUID.randomUUID().toString(),
    var title: String = "",
    var content: String = "",
    var createdBy: String = "",
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long? = null,
    var author: User? = null,
) {

    companion object {
        fun fromPost(post: Post, author: User? = null): PostDetail = PostDetail(
            id = post.id,
            title = post.title,
            content = post.content,
            createdBy = post.createdBy,
            createdAt = post.createdAt,
            updatedAt = post.updatedAt,
            author = author,
        )
    }
}