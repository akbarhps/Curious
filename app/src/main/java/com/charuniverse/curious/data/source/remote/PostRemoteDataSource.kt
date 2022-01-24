package com.charuniverse.curious.data.source.remote

import com.charuniverse.curious.data.dto.PostDetail
import com.charuniverse.curious.data.model.Post
import com.charuniverse.curious.util.Preferences
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import kotlinx.coroutines.tasks.await

class PostRemoteDataSource(
    firebaseDatabase: FirebaseDatabase,
) {

    private val postRef = firebaseDatabase.reference.child("posts")

    suspend fun getAll(page: Int, limit: Int): List<PostDetail> {
        return postRef
            .orderByChild("createdAt")
            .get()
            .await()
            .children.map { it.getValue(PostDetail::class.java)!! }
    }

    suspend fun getById(postId: String): PostDetail? {
        return postRef
            .child(postId)
            .orderByChild("comments/createdAt")
            .get()
            .await()
            .getValue(PostDetail::class.java)
    }

    suspend fun getByUserId(userId: String): List<PostDetail> {
        return postRef
            .orderByChild("createdBy")
            .equalTo(userId)
            .get()
            .await()
            .children.map { it.getValue(PostDetail::class.java)!! }
    }

    suspend fun create(post: Post) {
        postRef.child(post.id).setValue(post).await()
    }

    suspend fun update(post: Post) {
        val updates: MutableMap<String, Any> = HashMap()

        updates["${post.id}/title"] = post.title
        updates["${post.id}/content"] = post.content
        updates["${post.id}/updatedAt"] = post.updatedAt ?: System.currentTimeMillis()

        postRef.updateChildren(updates).await()
    }

    suspend fun delete(postId: String) {
        postRef.child(postId).setValue(null).await()
    }

    suspend fun toggleLove(postId: String, hasLike: Boolean) {
        val uid = Preferences.userId

        val updates: MutableMap<String, Any?> = HashMap()
        if (!hasLike) {
            updates["$postId/lovers/$uid"] = System.currentTimeMillis()
        } else {
            updates["$postId/lovers/$uid"] = null
        }

        postRef.updateChildren(updates).await()
    }
}