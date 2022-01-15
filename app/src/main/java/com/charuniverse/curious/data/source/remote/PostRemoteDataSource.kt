package com.charuniverse.curious.data.source.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.Result.*
import com.charuniverse.curious.data.entity.Post
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PostRemoteDataSource(
    firebaseDatabase: FirebaseDatabase,
    private val dispatcherContext: CoroutineDispatcher = Dispatchers.IO,
) {

    private val postRef = firebaseDatabase.reference.child("posts")

    private val observablePosts = MutableLiveData<Result<List<Post>>>()

    suspend fun refreshPosts() = withContext(Dispatchers.Main) {
        observablePosts.value = findAll()!!
    }

    fun observePosts(): LiveData<Result<List<Post>>> {
        return observablePosts
    }

    fun observePost(postId: String): LiveData<Result<Post>> {
        return observablePosts.map { posts ->
            when (posts) {
                is Loading -> Loading
                is Error -> Error(posts.exception)
                is Success -> {
                    val post = posts.data.firstOrNull { it.id == postId }
                        ?: return@map Error(IllegalArgumentException("Post not found"))

                    Success(post)
                }
            }
        }
    }

    suspend fun findByUserId(userId: String): Result<List<Post>> = withContext(dispatcherContext) {
        return@withContext try {
            val docs = postRef.orderByChild("createdBy")
                .equalTo(userId)
                .get().await()
            Success(docs.children.map { it.getValue(Post::class.java)!! })
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun findAll(): Result<List<Post>> = withContext(dispatcherContext) {
        return@withContext try {
            val docs = postRef.orderByChild("createdAt")
                .get().await()
            Success(docs.children.map { it.getValue(Post::class.java)!! })
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun findById(postId: String): Result<Post> = withContext(dispatcherContext) {
        return@withContext try {
            val doc = postRef.child(postId).get().await()
            val post = doc.getValue(Post::class.java)
                ?: throw IllegalArgumentException("Post not found")
            Success(post)
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun save(post: Post): Result<Unit> = withContext(dispatcherContext) {
        return@withContext try {
            postRef.child(post.id).setValue(post).await()
            Success(Unit)
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun delete(postId: String): Result<Unit> = withContext(dispatcherContext) {
        return@withContext try {
            postRef.child(postId).setValue(null).await()
            Success(Unit)
        } catch (e: Exception) {
            Error(e)
        }
    }

}