package com.charuniverse.curious.ui.profile.edit

import androidx.lifecycle.*
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.entity.User
import com.charuniverse.curious.data.repository.UserRepository
import com.charuniverse.curious.ui.post.BaseViewState
import com.charuniverse.curious.util.Event
import com.charuniverse.curious.util.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _viewState = MutableLiveData<Event<BaseViewState>>()
    val viewState: LiveData<Event<BaseViewState>> = _viewState

    private val _user = userRepository.observeUser()
        .distinctUntilChanged().switchMap { return@switchMap handleUserResult(it) }
    val user: LiveData<User?> = _user

    private fun handleUserResult(result: Result<User>): LiveData<User?> {
        return if (result is Result.Error) MutableLiveData(null)
        else MutableLiveData((result as Result.Success).data)
    }

    init {
        refreshUser()
    }

    private fun refreshUser() = CoroutineScope(Dispatchers.IO).launch {
        userRepository.refreshObservableUser(Preferences.userId)
    }

    fun updateUser(username: String, displayName: String) = viewModelScope.launch {
        _viewState.value = Event(BaseViewState(isLoading = true))
        val user = user.value!!

        user.username = username
        user.displayName = displayName

        userRepository.update(user)
        _viewState.value = Event(BaseViewState(isCompleted = true))
        refreshUser()
    }

}