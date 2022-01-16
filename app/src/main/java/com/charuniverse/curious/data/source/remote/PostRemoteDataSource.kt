package com.charuniverse.curious.data.source.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.Result.*
import com.charuniverse.curious.data.entity.Post
import com.charuniverse.curious.data.entity.User
import com.charuniverse.curious.data.model.PostDetail
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
    private val userRef = firebaseDatabase.reference.child("users")

    private var cachedUser = mutableMapOf<String, User>()

    private val observablePosts = MutableLiveData<Result<List<PostDetail>>>()
    fun observePosts(): LiveData<Result<List<PostDetail>>> = observablePosts

    suspend fun refreshPosts() = withContext(Dispatchers.Main) {
        observablePosts.value = findAll()!!
    }

    private val observableUserPosts = MutableLiveData<Result<List<Post>>>()
    fun observeUserPosts(userId: String): LiveData<Result<List<Post>>> = observableUserPosts

    suspend fun refreshUserPosts(userId: String) = withContext(Dispatchers.Main) {
        observableUserPosts.value = findByUserId(userId)!!
    }

    fun observePost(postId: String): LiveData<Result<PostDetail>> {
        return observablePosts.map { posts ->
            val result = when (posts) {
                is Loading -> Loading
                is Error -> Error(posts.exception)
                is Success -> {
                    val post = posts.data.firstOrNull { it.id == postId }!!
                    Success(post)
                }
            }
            result
        }
    }

    suspend fun findAll(): Result<List<PostDetail>> = withContext(dispatcherContext) {
        return@withContext try {
            val docs = postRef.orderByChild("createdAt")
                .get().await()

            Success(docs.children.map {
                val post = it.getValue(Post::class.java)!!

                val author = cachedUser[post.createdBy]
                    ?: userRef.child(post.createdBy).get().await()
                        .getValue(User::class.java)!!

                post.toFeedPost(author)
            })
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun findById(postId: String): Result<PostDetail> =
        withContext(dispatcherContext) {
            return@withContext try {
                val post = postRef.child(postId).get().await()
                    .getValue(Post::class.java)!!

                val author = cachedUser[post.createdBy]
                    ?: userRef.child(post.createdBy).get().await()
                        .getValue(User::class.java)!!

                Success(post.toFeedPost(author))
            } catch (e: Exception) {
                Error(e)
            }
        }

    suspend fun findByUserId(userId: String): Result<List<Post>> = withContext(dispatcherContext) {
        return@withContext try {
            val docs = postRef.orderByChild("createdBy")
                .equalTo(userId)
                .orderByChild("createdAt")
                .get().await()
            Success(docs.children.map { it.getValue(Post::class.java)!! })
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