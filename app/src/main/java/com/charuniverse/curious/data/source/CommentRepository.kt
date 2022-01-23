package com.charuniverse.curious.data.source

import android.util.Log
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.model.Comment
import com.charuniverse.curious.data.model.NotificationEvent
import com.charuniverse.curious.data.source.in_memory.InMemoryPostDataSource
import com.charuniverse.curious.data.source.remote.CommentRemoteDataSource
import com.charuniverse.curious.data.source.remote.NotificationRemoteDataSource
import com.charuniverse.curious.util.NotificationBuilder
import com.charuniverse.curious.util.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class CommentRepository(
    private val commentRemoteDataSource: CommentRemoteDataSource,
    private val notificationRemoteDataSource: NotificationRemoteDataSource,
) {

    private val inMemoryPost = InMemoryPostDataSource

    private fun sendNotification(comment: Comment) = CoroutineScope(Dispatchers.IO).launch {
        val eventId = comment.postId
        val eventType = NotificationEvent.POST_COMMENT

        val notification = NotificationBuilder
            .postNotification(eventId, eventType, comment.content)
            ?: return@launch

        try {
            notificationRemoteDataSource.create(notification)
        } catch (e: Exception) {
            Log.e("Notifications", "sendNotification: ${e.message}", e)
        }
    }

    suspend fun create(comment: Comment): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)

            commentRemoteDataSource.create(comment)
            inMemoryPost.addPostComment(comment)

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }

        sendNotification(comment)
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
        //TODO: Implement force refresh
//        if (!forceRefresh) {
//            return@flow
//        }

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