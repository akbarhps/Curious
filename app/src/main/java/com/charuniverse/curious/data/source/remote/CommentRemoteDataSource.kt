package com.charuniverse.curious.data.source.remote

import com.charuniverse.curious.data.model.Comment
import com.charuniverse.curious.data.source.CommentDataSource
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
        updates["${comment.postId}/comments/${comment.id}/updatedAt"] = comment.updatedAt ?: System.currentTimeMillis()

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

}