package com.charuniverse.curious.data.source.remote

import com.charuniverse.curious.data.dto.CommentDetail
import com.charuniverse.curious.data.model.Comment
import com.charuniverse.curious.util.Preferences
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import kotlinx.coroutines.tasks.await

class CommentRemoteDataSource(
    firebaseDatabase: FirebaseDatabase,
) {

    private val postRef = firebaseDatabase.reference.child("posts")

    suspend fun getById(postId: String, commentId: String): CommentDetail? {
        return postRef.child("${postId}/comments/${commentId}")
            .get()
            .await()
            .getValue(CommentDetail::class.java)
    }

    suspend fun create(comment: Comment) {
        postRef
            .child("${comment.postId}/comments/${comment.id}")
            .setValue(comment)
            .await()
    }

    suspend fun update(comment: Comment) {
        val updates: MutableMap<String, Any> = HashMap()

        updates["${comment.postId}/comments/${comment.id}/content"] = comment.content
        updates["${comment.postId}/comments/${comment.id}/updatedAt"] =
            comment.updatedAt ?: System.currentTimeMillis()

        postRef.updateChildren(updates).await()
    }

    suspend fun delete(postId: String, commentId: String) {
        postRef
            .child("${postId}/comments/${commentId}")
            .setValue(null)
            .await()
    }

    suspend fun toggleLove(postId: String, commentId: String, hasLove: Boolean) {
        val uid = Preferences.userId
        val updates: MutableMap<String, Any?> = HashMap()

        if (!hasLove) {
            updates["${postId}/comments/${commentId}/lovers/${uid}"] = System.currentTimeMillis()
        } else {
            updates["${postId}/comments/${commentId}/lovers/${uid}"] = null
        }

        postRef.updateChildren(updates).await()
    }
}