package com.charuniverse.curious.ui.post.detail

import android.util.Log
import androidx.lifecycle.*
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.model.PostDetail
import com.charuniverse.curious.data.repository.PostRepository
import com.charuniverse.curious.exception.NotFound
import com.charuniverse.curious.ui.post.BaseViewState
import com.charuniverse.curious.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val postRepository: PostRepository,
) : ViewModel() {

    companion object {
        private const val TAG = "PostDetailViewModel"
    }

    private val _viewState = MutableLiveData<Event<BaseViewState>>()
    val viewState: LiveData<Event<BaseViewState>> = _viewState

    private fun updateState(
        isLoading: Boolean = false, error: Exception? = null, isCompleted: Boolean = false,
    ) {
        _viewState.value = Event(
            BaseViewState(isLoading = isLoading, error = error, isCompleted = isCompleted)
        )
    }

    private val _postId = MutableLiveData<String>()

    fun setPostId(id: String) {
        if (id.isBlank()) {
            updateState(error = NotFound("Post not found"))
            return
        }

        updateState(isLoading = true)
        _postId.value = id
    }

    private val _post = _postId.switchMap { id ->
        postRepository.observePost(id).map { handlePostResult(it) }
    }
    val post: LiveData<PostDetail> = _post

    private fun handlePostResult(result: Result<PostDetail>): PostDetail {
        return when (result) {
            is Result.Loading -> PostDetail()
            is Result.Error -> {
                Log.e(TAG, "handleResult: ${result.exception.message}", result.exception)
                updateState(error = result.exception)
                PostDetail()
            }
            is Result.Success -> {
                updateState(isLoading = false)
                result.data
            }
        }
    }

    private val _selectedUserId = MutableLiveData<Event<String>>()
    val selectedUserId: LiveData<Event<String>> = _selectedUserId

    fun setSelectedUserId(userId: String) {
        _selectedUserId.value = Event(userId)
    }

    fun refreshPost() = CoroutineScope(Dispatchers.IO).launch {
        postRepository.refreshPosts()
    }

    fun togglePostLove() = viewModelScope.launch {
        postRepository.toggleLove(_postId.value!!)
        refreshPost()
    }

    fun deleteComment(commentId: String) = viewModelScope.launch {
        postRepository.deleteComment(_postId.value!!, commentId)
        refreshPost()
    }

    fun deletePost() = viewModelScope.launch {
        val result = postRepository.delete(_postId.value!!)
        if (result is Result.Error) {
            Log.e(TAG, "deletePost: ${result.exception.message}", result.exception)
            updateState(error = result.exception)
        } else {
            refreshPost()
            updateState(isCompleted = true)
        }
    }
}