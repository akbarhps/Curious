package com.charuniverse.curious.data.dto

import com.charuniverse.curious.data.model.Comment
import com.charuniverse.curious.data.model.User
import java.util.*

data class CommentDetail(
    var id: String = UUID.randomUUID().toString(),
    var postId: String = "",
    var content: String = "",
    var createdBy: String = "",
    var createdAt: Long = 0L,
    var updatedAt: Long? = null,
    var loveCount: Long = 0L,
    var lovers: MutableMap<String, Long> = mutableMapOf(),
    var isViewerLoved: Boolean = false,
    var author: User? = null,
) {
    fun toCommentEntity(): Comment = Comment(
        id = this.id,
        postId = this.postId,
        content = this.content,
        createdBy = this.createdBy,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )

    companion object {
        fun fromCommentEntity(comment: Comment): CommentDetail = CommentDetail(
            id = comment.id,
            postId = comment.postId,
            content = comment.content,
            createdBy = comment.createdBy,
            createdAt = comment.createdAt,
            updatedAt = comment.updatedAt,
        )
    }
}
