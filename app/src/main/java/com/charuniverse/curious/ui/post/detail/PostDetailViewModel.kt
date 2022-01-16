package com.charuniverse.curious.ui.post.detail

import android.util.Log
import androidx.lifecycle.*
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.model.PostDetail
import com.charuniverse.curious.data.repository.PostRepository
import com.charuniverse.curious.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    companion object {
        private const val TAG = "PostDetailViewModel"
    }

    private val _viewState = MutableLiveData<Event<PostDetailViewState>>()
    val viewState: LiveData<Event<PostDetailViewState>> = _viewState

    private fun updateState(
        isLoading: Boolean = false, isCompleted: Boolean = false, error: String? = null
    ) {
        _viewState.value = Event(
            PostDetailViewState(isLoading = isLoading, error = error, isCompleted = isCompleted)
        )
    }


    private val _postId = MutableLiveData<String>()

    fun setPostId(id: String) {
        if (id.isBlank()) {
            updateState(error = "No Post Id Provided")
            return
        }

        updateState(isLoading = true)
        _postId.value = id
    }

    private val _post = _postId.switchMap { id ->
        postRepository.observePost(id).map { handleResult(it) }
    }
    val post: LiveData<PostDetail?> = _post

    private fun handleResult(result: Result<PostDetail>): PostDetail? {
        return if (result is Result.Error) {
            Log.e(TAG, "handleResult: ${result.exception.message}", result.exception)
            updateState(error = result.exception.message.toString())
            null
        } else {
            updateState(isLoading = false)
            (result as Result.Success).data
        }
    }

    fun deletePost() = viewModelScope.launch {
        when (val result = postRepository.delete(_postId.value!!)) {
            is Result.Error -> {
                Log.e(TAG, "deletePost: ${result.exception.message}", result.exception)
                updateState(error = result.exception.message.toString())
            }
            is Result.Loading -> updateState(isLoading = true)
            is Result.Success -> updateState(isCompleted = true)
        }
    }
}