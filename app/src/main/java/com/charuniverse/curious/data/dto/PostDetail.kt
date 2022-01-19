package com.charuniverse.curious.data.dto

import com.charuniverse.curious.data.model.Post
import com.charuniverse.curious.data.model.User
import java.util.*

data class PostDetail(
    var id: String = UUID.randomUUID().toString(),
    var title: String = "",
    var content: String = "",
    var createdBy: String = "",
    var createdAt: Long = 0L,
    var updatedAt: Long? = null,
    var author: User? = null,
    var loveCount: Long = 0L,
    var lovers: MutableMap<String, Long> = mutableMapOf(),
    var isViewerLoved: Boolean = false,
    var commentCount: Long = 0L,
    var comments: MutableMap<String, CommentDetail> = mutableMapOf(),
) {
    fun toDomainPost(): Post = Post(
        id = this.id,
        title = this.title,
        content = this.content,
        createdBy = this.createdBy,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )

    companion object {
        fun fromDomainPost(post: Post): PostDetail = PostDetail(
            id = post.id,
            title = post.title,
            content = post.content,
            createdBy = post.createdBy,
            createdAt = post.createdAt,
            updatedAt = post.updatedAt,
        )
    }
}