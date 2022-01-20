package com.charuniverse.curious.data.source

import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.model.Comment
import com.charuniverse.curious.data.model.NotificationData
import com.charuniverse.curious.data.source.in_memory.InMemoryPostDataSource
import com.charuniverse.curious.data.source.remote.CommentRemoteDataSource
import com.charuniverse.curious.data.source.remote.NotificationAPI
import com.charuniverse.curious.util.NotificationBuilder
import com.charuniverse.curious.util.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CommentRepository(
    private val commentRemoteDataSource: CommentRemoteDataSource,
    private val notificationAPI: NotificationAPI,
) {

    private val inMemoryPost = InMemoryPostDataSource

    suspend fun create(comment: Comment): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)

            commentRemoteDataSource.create(comment)
            inMemoryPost.addPostComment(comment)

            emit(Result.Success(Unit))

            val notification = NotificationBuilder.build(
                comment.postId,
                NotificationData.EVENT_POST_COMMENT,
                comment.content,
            ) ?: return@flow emit(Result.Success(Unit))

            notificationAPI.send(notification)
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    suspend fun update(comment: Comment): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)

            commentRemoteDataSource.update(comment)
            inMemoryPost.updatePostComment(comment)

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    suspend fun delete(postId: String, commentId: String): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)

            commentRemoteDataSource.delete(postId, commentId)
            inMemoryPost.deletePostComment(postId, commentId)

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    suspend fun getById(
        postId: String,
        commentId: String,
        forceRefresh: Boolean = false
    ): Flow<Result<Comment>> = flow {
        if (!forceRefresh) {
            return@flow
        }

        emit(Result.Loading)
        val post = inMemoryPost.getById(postId)
        if (post != null) {
            val comment = post.comments[commentId]
            if (comment != null) {
                return@flow emit(Result.Success(comment.toCommentEntity()))
            }
        }
    }

    suspend fun toggleLove(postId: String, commentId: String): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)

            val comment = commentRemoteDataSource.getById(postId, commentId)
                ?: throw IllegalArgumentException("Comment not found")

            val hasLove = comment.lovers.containsKey(Preferences.userId)
            commentRemoteDataSource.toggleLove(postId, commentId, hasLove)

            inMemoryPost.togglePostCommentLove(postId, commentId, hasLove)

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}