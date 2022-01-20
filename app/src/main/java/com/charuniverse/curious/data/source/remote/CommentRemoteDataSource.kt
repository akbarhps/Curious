package com.charuniverse.curious.data.source.remote

import com.charuniverse.curious.data.dto.CommentDetail
import com.charuniverse.curious.data.model.Comment
import com.charuniverse.curious.data.source.CommentDataSource
import com.charuniverse.curious.util.Preferences
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

class CommentRemoteDataSource(
    firebaseDatabase: FirebaseDatabase,
    private val context: CoroutineContext = Dispatchers.IO,
) : CommentDataSource {

    private val postRef = firebaseDatabase.reference.child("posts")

    suspend fun getById(postId: String, commentId: String): Flow<CommentDetail?> = flow {
        val comment = postRef.child("${postId}/comments/${commentId}")
            .get()
            .await()
            .getValue(CommentDetail::class.java)

        emit(comment)
    }

    override suspend fun create(comment: Comment): Flow<Unit> = flow {
        val updates: MutableMap<String, Any> = HashMap()

        updates["${comment.postId}/comments/${comment.id}"] = comment
        updates["${comment.postId}/commentCount"] = ServerValue.increment(1)

        postRef.updateChildren(updates).await()
        emit(Unit)
    }

    override suspend fun update(comment: Comment): Flow<Unit> = flow {
        val updates: MutableMap<String, Any> = HashMap()

        updates["${comment.postId}/comments/${comment.id}/content"] = comment.content
        updates["${comment.postId}/comments/${comment.id}/updatedAt"] =
            comment.updatedAt ?: System.currentTimeMillis()

        postRef.updateChildren(updates).await()
        emit(Unit)
    }

    override suspend fun delete(postId: String, commentId: String): Flow<Unit> = flow {
        val updates: MutableMap<String, Any?> = HashMap()

        updates["${postId}/comments/${commentId}"] = null
        updates["${postId}/commentCount"] = ServerValue.increment(-1)

        postRef.updateChildren(updates)
        emit(Unit)
    }

    suspend fun toggleLove(
        postId: String,
        commentId: String,
        hasLove: Boolean
    ): Flow<Unit> = flow {
        val uid = Preferences.userId
        val updates: MutableMap<String, Any?> = HashMap()

        if (!hasLove) {
            updates["${postId}/comments/${commentId}/lovers/${uid}"] = System.currentTimeMillis()
            updates["${postId}/comments/${commentId}/loveCount"] = ServerValue.increment(1)
        } else {
            updates["${postId}/comments/${commentId}/lovers/${uid}"] = null
            updates["${postId}/comments/${commentId}/loveCount"] = ServerValue.increment(-1)
        }

        postRef.updateChildren(updates).await()
        emit(Unit)
    }
}