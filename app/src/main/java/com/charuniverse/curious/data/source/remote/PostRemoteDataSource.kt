package com.charuniverse.curious.data.source.remote

import com.charuniverse.curious.data.dto.PostDetail
import com.charuniverse.curious.data.model.Post
import com.charuniverse.curious.data.source.PostDataSource
import com.charuniverse.curious.util.Preferences
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

class PostRemoteDataSource(
    firebaseDatabase: FirebaseDatabase,
    private val context: CoroutineContext = Dispatchers.IO,
) : PostDataSource {

    private val postRef = firebaseDatabase.reference.child("posts")

    override suspend fun getAll(page: Int, limit: Int): Flow<List<PostDetail>> = flow {
        val posts = postRef
            .orderByChild("createdAt")
            .get()
            .await()
            .children.map { it.getValue(PostDetail::class.java)!! }

        emit(posts)
    }

    override suspend fun getById(postId: String): Flow<PostDetail?> = flow {
        val post = postRef
            .child(postId)
            .orderByChild("comments/createdAt")
            .get()
            .await()
            .getValue(PostDetail::class.java)


        emit(post)
    }

    override suspend fun getByUserId(userId: String): Flow<List<PostDetail>> = flow {
        val posts = postRef
            .orderByChild("createdBy")
            .equalTo(userId)
            .get()
            .await()
            .children.map { it.getValue(PostDetail::class.java)!! }

        emit(posts)
    }

    override suspend fun create(post: Post): Flow<Unit> = flow {
        postRef.child(post.id).setValue(post).await()
        emit(Unit)
    }

    override suspend fun update(post: Post): Flow<Unit> = flow {
        val updates: MutableMap<String, Any> = HashMap()

        updates["${post.id}/title"] = post.title
        updates["${post.id}/content"] = post.content
        updates["${post.id}/updatedAt"] = post.updatedAt ?: System.currentTimeMillis()

        postRef.updateChildren(updates).await()
        emit(Unit)
    }

    override suspend fun delete(postId: String): Flow<Unit> = flow {
        postRef.child(postId).setValue(null).await()
        emit(Unit)
    }

    override suspend fun toggleLove(postId: String, hasLike: Boolean): Flow<Unit> = flow {
        val uid = Preferences.userId

        val updates: MutableMap<String, Any?> = HashMap()
        if (!hasLike) {
            updates["$postId/lovers/$uid"] = System.currentTimeMillis()
            updates["$postId/loveCount"] = ServerValue.increment(1)
        } else {
            updates["$postId/lovers/$uid"] = null
            updates["$postId/loveCount"] = ServerValue.increment(-1)
        }

        postRef.updateChildren(updates).await()
        emit(Unit)
    }
}