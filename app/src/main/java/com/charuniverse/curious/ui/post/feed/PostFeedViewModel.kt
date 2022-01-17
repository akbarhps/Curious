package com.charuniverse.curious.ui.post.feed

import android.util.Log
import androidx.lifecycle.*
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.model.PostDetail
import com.charuniverse.curious.data.repository.PostRepository
import com.charuniverse.curious.ui.post.PostViewState
import com.charuniverse.curious.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostFeedViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    companion object {
        private const val TAG = "PostViewModel"
    }

    private val _viewState = MutableLiveData<Event<PostViewState>>()
    val viewState: LiveData<Event<PostViewState>> = _viewState

    private fun updateViewState(isLoading: Boolean = false, error: Exception? = null) {
        _viewState.value = Event(PostViewState(isLoading = isLoading, error = error))
    }

    private val _posts: LiveData<List<PostDetail>> = postRepository.observePosts()
        .distinctUntilChanged().switchMap { return@switchMap handleResult(it) }
    val posts: LiveData<List<PostDetail>> = _posts

    private val _selectedPostId = MutableLiveData<Event<String>>()
    val selectedPostId: LiveData<Event<String>> = _selectedPostId

    fun setSelectedPostId(postId: String) {
        _selectedPostId.value = Event(postId)
    }

    init {
        refreshPosts()
    }

    fun refreshPosts() = viewModelScope.launch {
        updateViewState(isLoading = true)
        postRepository.refreshPosts()
    }

    fun toggleLove(postId: String) = viewModelScope.launch {
        postRepository.toggleLove(postId)
        refreshPosts()
    }

    private fun handleResult(result: Result<List<PostDetail>>): LiveData<List<PostDetail>> {
        updateViewState(isLoading = false)
        return when (result) {
            is Result.Loading -> MutableLiveData(listOf()) // this never occur
            is Result.Success -> {
                val data = result.data.sortedByDescending { it.createdAt }
                MutableLiveData(data)
            }
            is Result.Error -> {
                Log.e(TAG, "handleResult: ${result.exception.message}", result.exception)
                MutableLiveData(listOf())
            }
        }
    }

}