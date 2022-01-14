package com.charuniverse.curious.ui.post.feed

import android.util.Log
import androidx.lifecycle.*
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.entity.Post
import com.charuniverse.curious.data.entity.User
import com.charuniverse.curious.data.model.PostFeedResponse
import com.charuniverse.curious.data.repository.PostRepository
import com.charuniverse.curious.data.repository.UserRepository
import com.charuniverse.curious.data.succeeded
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostFeedViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _posts: LiveData<List<PostFeedResponse>> =
        postRepository.observePosts().distinctUntilChanged().switchMap {
            return@switchMap filterPost(it)
        }

    val posts: LiveData<List<PostFeedResponse>> = _posts

    private val cachedUser = mutableMapOf<String, User>()

    init {
        viewModelScope.launch {
            postRepository.refreshPost()
        }
    }

    fun refresh() = viewModelScope.launch {
        postRepository.refreshPost()
    }

    private fun filterPost(postsResult: Result<List<Post>>): LiveData<List<PostFeedResponse>> {
        val result = MutableLiveData<List<PostFeedResponse>>()

        if (postsResult is Result.Success) {
            viewModelScope.launch {
                result.value = postsResult.data.map {
                    if (cachedUser[it.createdBy] == null) {
                        val postAuthor = userRepository.findById(it.createdBy)
                        if (postAuthor.succeeded) {
                            val user = (postAuthor as Result.Success).data
                            cachedUser[user.id] = user
                        }
                    }

                    PostFeedResponse(
                        id = it.id,
                        title = it.title,
                        body = it.body,
                        author = cachedUser[it.createdBy],
                        createdBy = it.createdBy,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt,
                    )
                }
            }
        } else if (postsResult is Result.Error) {
            result.value = emptyList()
            Log.e(
                "PostViewModel",
                "filterPost: ${postsResult.exception.message}",
                postsResult.exception
            )
        }

        return result
    }

}