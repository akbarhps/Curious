package com.charuniverse.curious.data.source.in_memory

import com.charuniverse.curious.data.dto.CommentDetail
import com.charuniverse.curious.data.dto.PostDetail
import com.charuniverse.curious.data.model.Comment
import com.charuniverse.curious.data.model.Post
import com.charuniverse.curious.util.Preferences

object InMemoryPostDataSource {

    private var posts = mutableMapOf<String, PostDetail>()

    fun getAllAsList(): List<PostDetail> = posts.map { it.value }

    fun getByUserId(userId: String): List<PostDetail> =
        posts.filter { it.value.createdBy == userId }
            .values
            .toList()

    fun add(data: PostDetail) = posts.put(data.id, data)

    fun add(post: Post) = PostDetail.fromDomainPost(post).let {
        it.author = InMemoryUserDataSource.getById(it.createdBy)
        posts[it.id] = it
    }

    fun addPostComment(comment: Comment) = CommentDetail.fromCommentEntity(comment).let {
        it.author = InMemoryUserDataSource.getById(it.createdBy)
        posts[comment.postId]?.let { post ->
            post.comments[comment.id] = it
            post.commentCount++
        }
    }

    fun getById(postId: String): PostDetail? = posts[postId]

    fun delete(postId: String) = posts.remove(postId)

    fun updatePost(post: Post) = posts[post.id]?.let {
        it.title = post.title
        it.content = post.content
        it.updatedAt = post.updatedAt
    }

    fun togglePostLove(postId: String, hasLike: Boolean) = posts[postId]?.let {
        val uid = Preferences.userId
        if (!hasLike) {
            it.lovers[uid] = System.currentTimeMillis()
            it.loveCount++
        } else {
            it.lovers.remove(uid)
            it.loveCount--
        }
        it.isViewerLoved = !hasLike
    }

    fun updatePostComment(comment: Comment) = posts[comment.postId]?.let {
        val cacheComment = it.comments[comment.id] ?: return@let
        cacheComment.content = comment.content
        cacheComment.updatedAt = comment.updatedAt
    }

    fun togglePostCommentLove(postId: String, commentId: String, hasLove: Boolean) =
        posts[postId]?.let {
            val uid = Preferences.userId
            val comment = it.comments[commentId] ?: return@let

            if (!hasLove) {
                comment.lovers[uid] = System.currentTimeMillis()
                comment.loveCount++
            } else {
                comment.lovers.remove(uid)
                comment.loveCount--
            }

            comment.isViewerLoved = !hasLove
            it.comments[commentId] = comment
        }

    fun deletePostComment(postId: String, commentId: String) = posts[postId]?.let {
        if (it.comments.containsKey(commentId)) {
            it.comments.remove(commentId)
            it.commentCount--
        }
    }

    fun clear() = posts.clear()

}