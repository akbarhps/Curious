package com.charuniverse.curious.ui.profile

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.dto.PostDetail
import com.charuniverse.curious.data.model.User
import com.charuniverse.curious.data.source.AuthRepository
import com.charuniverse.curious.data.source.PostRepository
import com.charuniverse.curious.data.source.UserRepository
import com.charuniverse.curious.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    companion object {
        private const val TAG = "VMProfile"
    }

    private val _viewState = MutableLiveData<Event<ProfileViewState>>()
    val viewState: LiveData<Event<ProfileViewState>> = _viewState

    private fun updateViewState(
        isLoading: Boolean = false,
        isLoggedOut: Boolean = false,
        error: Exception? = null,
        selectedPostId: String? = null,
    ) {
        _viewState.value = Event(
            ProfileViewState(
                isLoading = isLoading,
                error = error,
                isLoggedOut = isLoggedOut,
                selectedPostId = selectedPostId,
            )
        )
    }

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _userPosts = MutableLiveData<List<PostDetail>>()
    val userPosts: LiveData<List<PostDetail>> = _userPosts

    private lateinit var currentUserId: String

    fun setUserId(userId: String) {
        currentUserId = userId
        refresh(false)
    }

    fun refresh(forceRefresh: Boolean) {
        refreshUser(forceRefresh)
        refreshUserPost(forceRefresh)
    }

    private fun refreshUser(forceRefresh: Boolean = false) = viewModelScope.launch {
        userRepository.getById(currentUserId, forceRefresh).collect { res ->
            resultHandler(res) {
                _user.value = it
                updateViewState()
            }
        }
    }

    private fun refreshUserPost(forceRefresh: Boolean = false) = viewModelScope.launch {
        postRepository.observeUserPosts(currentUserId, forceRefresh).collect { res ->
            resultHandler(res) { posts ->
                _userPosts.value = posts.sortedByDescending { it.createdAt }
                updateViewState()
            }
        }
    }

    fun setSelectedPostId(postId: String) {
        updateViewState(selectedPostId = postId)
    }

    fun toggleLove(postId: String) = viewModelScope.launch {
        postRepository.toggleLove(postId).collect {
            resultHandler(it) { refreshUserPost(false) }
        }
    }

    fun logOut(context: Context) = viewModelScope.launch {
        authRepository.logOut(context).collect { res ->
            resultHandler(res) { updateViewState(isLoggedOut = true) }
        }
    }

    private fun <T> resultHandler(result: Result<T>, onSuccess: (T) -> Unit) {
        when (result) {
            is Result.Loading -> updateViewState(isLoading = true)
            is Result.Error -> {
                Log.e(TAG, "resultHandler: ${result.exception.message}", result.exception)
                updateViewState(error = result.exception)
            }
            is Result.Success -> onSuccess(result.data)
        }
    }
}