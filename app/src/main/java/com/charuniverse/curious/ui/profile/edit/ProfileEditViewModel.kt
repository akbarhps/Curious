package com.charuniverse.curious.ui.profile.edit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.model.User
import com.charuniverse.curious.data.source.UserRepository
import com.charuniverse.curious.util.Event
import com.charuniverse.curious.util.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        private const val TAG = "VMProfileEdit"
    }

    private val _viewState = MutableLiveData<Event<ProfileEditViewState>>()
    val viewState: LiveData<Event<ProfileEditViewState>> = _viewState

    private fun updateState(
        isLoading: Boolean = false,
        error: Exception? = null,
        isFinished: Boolean = false,
        user: User? = null,
    ) {
        _viewState.value = Event(
            ProfileEditViewState(
                isLoading = isLoading,
                error = error,
                isFinished = isFinished,
                user = user
            )
        )
    }

    init {
        refreshUser()
    }

    private var currentUser = User()

    private fun refreshUser() = viewModelScope.launch {
        userRepository.getById(Preferences.userId, true).collect { res ->
            resultHandler(res) {
                currentUser = it
                updateState(user = it)
            }
        }
    }

    fun updateUser(username: String, displayName: String) = viewModelScope.launch {
        // TODO: Implement change profile picture
        currentUser.username = username
        currentUser.displayName = displayName
        currentUser.updatedAt = System.currentTimeMillis()

        userRepository.update(currentUser).collect { res ->
            resultHandler(res) { updateState(isFinished = true) }
        }
    }

    private fun <T> resultHandler(result: Result<T>, onSuccess: (T) -> Unit) {
        when (result) {
            is Result.Loading -> updateState(isLoading = true)
            is Result.Error -> updateState(error = result.exception)
            is Result.Success -> onSuccess(result.data)
        }
    }
}