package com.charuniverse.curious.data.source.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.entity.Post
import com.charuniverse.curious.data.entity.User
import com.charuniverse.curious.data.model.PostDetail
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PostDetailRemoteDataSource(
    firebaseDatabase: FirebaseDatabase,
    private val dispatcherContext: CoroutineDispatcher = Dispatchers.IO
) {

    private val postRef = firebaseDatabase.reference.child("posts")
    private val userRef = firebaseDatabase.reference.child("users")

    private val observableDataList = MutableLiveData<Result<List<PostDetail>>>()
    fun observeDataList(): LiveData<Result<List<PostDetail>>> = observableDataList

    private var cachedUser = mutableMapOf<String, User>()

    suspend fun refreshData() = withContext(Dispatchers.Main) {
        observableDataList.value = findAll()!!
    }

    fun observeData(id: String): LiveData<Result<PostDetail>> {
        return observableDataList.map { posts ->
            when (posts) {
                is Result.Loading -> Result.Loading
                is Result.Error -> Result.Error(posts.exception)
                is Result.Success -> {
                    val post = posts.data.firstOrNull { it.id == id }!!
                    Result.Success(post)
                }
            }
        }
    }

    suspend fun findAll(): Result<List<PostDetail>> = withContext(dispatcherContext) {
        return@withContext try {
            val docs = postRef.orderByChild("createdAt")
                .get().await()

            Result.Success(docs.children.map {
                val post = it.getValue(Post::class.java)!!

                val author = cachedUser[post.createdBy]
                    ?: userRef.child(post.createdBy).get().await()
                        .getValue(User::class.java)!!

                post.toFeedPost(author)
            })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun findById(postId: String): Result<PostDetail> = withContext(dispatcherContext) {
        return@withContext try {
            val post = postRef.child(postId).get().await()
                .getValue(Post::class.java)!!

            val author = userRef.child(post.createdBy).get().await()
                .getValue(User::class.java)!!

            Result.Success(post.toFeedPost(author))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun delete(postId: String): Result<Unit> = withContext(dispatcherContext) {
        return@withContext try {
            postRef.child(postId).setValue(null).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}