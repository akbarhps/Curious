package com.charuniverse.curious.data.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.Result.*
import com.charuniverse.curious.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PostRemoteDataSource(
    firebaseFirestore: FirebaseFirestore,
    private val dispatcherContext: CoroutineDispatcher = Dispatchers.IO,
) {

    private val postRef = firebaseFirestore.collection("posts")

    private val observablePosts = MutableLiveData<Result<List<Post>>>()

    suspend fun refreshPosts() {
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
                    val post = posts.data.firstOrNull() { it.id == postId }
                        ?: return@map Error(IllegalArgumentException("Post not found"))

                    Success(post)
                }
            }
        }
    }

    suspend fun findAll(): Result<List<Post>> = withContext(dispatcherContext) {
        return@withContext try {
            val docs = postRef.get().await()
            Success(docs.toObjects(Post::class.java))
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun findById(id: String): Result<Post> = withContext(dispatcherContext) {
        return@withContext try {
            val doc = postRef.document(id).get().await()

            val post = doc.toObject(Post::class.java)
                ?: throw IllegalArgumentException("Post not found")

            Success(post)
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun save(post: Post): Result<Unit> = withContext(dispatcherContext) {
        return@withContext try {
            postRef.document(post.id).set(post).await()
            Success(Unit)
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun delete(postId: String): Result<Unit> = withContext(dispatcherContext) {
        return@withContext try {
            postRef.document(postId).delete().await()
            Success(Unit)
        } catch (e: Exception) {
            Error(e)
        }
    }

}