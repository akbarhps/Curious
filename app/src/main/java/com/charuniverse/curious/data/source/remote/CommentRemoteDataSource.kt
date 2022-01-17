package com.charuniverse.curious.data.source.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.Result.*
import com.charuniverse.curious.data.entity.Comment
import com.charuniverse.curious.data.entity.User
import com.charuniverse.curious.data.model.CommentDetail
import com.charuniverse.curious.util.Cache
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class CommentRemoteDataSource(
    firebaseDatabase: FirebaseDatabase,
    private val context: CoroutineContext = Dispatchers.IO,
) {

    private val userRef = firebaseDatabase.reference.child("users")
    private val postRef = firebaseDatabase.reference.child("posts")

    private val observableComments = MutableLiveData<Result<List<CommentDetail>>>()
    fun observeComments(): LiveData<Result<List<CommentDetail>>> = observableComments

    suspend fun refreshComments(postId: String) = withContext(Dispatchers.Main) {
        observableComments.value = findByPostId(postId)!!
    }

    suspend fun findByPostId(postId: String): Result<List<CommentDetail>> = withContext(context) {
        return@withContext try {
            val docs = postRef
                .child(postId).child("comments")
                .get().await()

            Success(docs.children.map {
                val comment = it.getValue(Comment::class.java)!!

                var author = Cache.users[comment.createdBy]
                if (author == null) {
                    val userDoc = userRef.child(comment.createdBy).get().await()
                    author = userDoc.getValue(User::class.java)!!
                }

                CommentDetail.fromComment(comment, author)
            })
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun save(comment: Comment): Result<Unit> = withContext(context) {
        return@withContext try {
            postRef.child(comment.postId)
                .child("comments")
                .child(comment.id)
                .setValue(comment)
                .await()
            Success(Unit)
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun delete(postId: String, commentId: String): Result<Unit> = withContext(context) {
        return@withContext try {
            postRef.child(postId)
                .child("comments")
                .child(commentId)
                .setValue(null).await()
            Success(Unit)
        } catch (e: Exception) {
            Error(e)
        }
    }
}