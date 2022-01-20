package com.charuniverse.curious.ui.post.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.dto.PostDetail
import com.charuniverse.curious.data.source.PostRepository
import com.charuniverse.curious.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostFeedViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    companion object {
        private const val TAG = "PostViewModel"
    }

    private val _viewState = MutableLiveData<Event<PostFeedViewState>>()
    val viewState: LiveData<Event<PostFeedViewState>> = _viewState

    private fun updateState(
        isLoading: Boolean = false,
        error: Exception? = null,
        posts: List<PostDetail>? = null,
        selectedPostId: String? = null,
    ) {
        _viewState.value = Event(
            PostFeedViewState(
                isLoading = isLoading,
                error = error,
                posts = posts,
                selectedPostId = selectedPostId,
            )
        )
    }

    init {
        refreshPosts()
    }

    fun refreshPosts(forceRefresh: Boolean = false) = viewModelScope.launch {
        postRepository.observePosts(forceRefresh = forceRefresh).collect { result ->
            when (result) {
                is Result.Loading -> updateState(isLoading = true)
                is Result.Error -> updateState(error = result.exception)
                is Result.Success -> updateState(posts = result.data.sortedByDescending { it.createdAt })
            }
        }
    }

    fun togglePostLove(postId: String) = viewModelScope.launch {
        postRepository.toggleLove(postId).collect {
            when (it) {
                is Result.Loading -> updateState(isLoading = true)
                is Result.Error -> updateState(error = it.exception)
                is Result.Success -> refreshPosts(false)
            }
        }
    }

    fun setSelectedPostId(id: String) {
        updateState(selectedPostId = id)
    }
}