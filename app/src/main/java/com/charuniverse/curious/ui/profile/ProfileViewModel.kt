package com.charuniverse.curious.ui.profile

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.entity.User
import com.charuniverse.curious.data.model.PostDetail
import com.charuniverse.curious.data.repository.AuthRepository
import com.charuniverse.curious.data.repository.PostRepository
import com.charuniverse.curious.data.repository.UserRepository
import com.charuniverse.curious.util.Event
import com.charuniverse.curious.util.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    companion object {
        private const val TAG = "ProfileViewModel"
    }

    private val _viewState = MutableLiveData<Event<ProfileViewState>>()
    val viewState: LiveData<Event<ProfileViewState>> = _viewState

    private fun updateViewState(
        isLoading: Boolean = false, isSignedOut: Boolean = false, error: Exception? = null
    ) {
        _viewState.value =
            Event(ProfileViewState(isLoading = isLoading, isSignOut = isSignedOut, error = error))
    }

    private val _user = userRepository.observeUser()
        .distinctUntilChanged().switchMap { return@switchMap handleUserResult(it) }
    val user: LiveData<User?> = _user

    private fun handleUserResult(result: Result<User>): LiveData<User?> {
        return if (result is Result.Error) MutableLiveData(null)
        else MutableLiveData((result as Result.Success).data)
    }

    private val _userPosts = _user.switchMap { user ->
        if (user == null) return@switchMap MutableLiveData(listOf())
        postRepository.observeUserPosts(user.id)
            .distinctUntilChanged().switchMap { handleUserPostResult(it) }
    }
    val userPosts: LiveData<List<PostDetail>> = _userPosts

    private fun handleUserPostResult(result: Result<List<PostDetail>>): LiveData<List<PostDetail>> {
        return if (result is Result.Error) MutableLiveData(listOf())
        else MutableLiveData((result as Result.Success).data)
    }

    init {
        viewModelScope.launch {
            // TODO: change this to dynamic value
            userRepository.refreshObservableUser(Preferences.userId)
        }
    }

    fun toggleLove(postId: String) = viewModelScope.launch {
        postRepository.toggleLove(postId)
        postRepository.refreshPosts()
    }

    fun logOut(context: Context) = viewModelScope.launch {
        val result = authRepository.logOut(context)

        if (result is Result.Error) {
            Log.e(TAG, "logOut: ${result.exception.message}", result.exception)
            updateViewState(error = result.exception)
        } else {
            updateViewState(isSignedOut = true)
        }
    }

}