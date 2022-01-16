package com.charuniverse.curious.ui.profile

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.entity.User
import com.charuniverse.curious.data.repository.AuthRepository
import com.charuniverse.curious.data.repository.UserRepository
import com.charuniverse.curious.util.Event
import com.charuniverse.curious.util.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        private const val TAG = "ProfileViewModel"
    }

    private val _viewState = MutableLiveData<ProfileViewState>()
    val viewState: LiveData<ProfileViewState> = _viewState

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    private val _isLoggedOut = MutableLiveData<Event<Boolean>>()
    val isLoggedOut: LiveData<Event<Boolean>> = _isLoggedOut

    init {
        getUser()
    }

    private fun getUser() = viewModelScope.launch {
        _viewState.value = ProfileViewState(isLoading = true)

        val loginUser = authRepository.getLoginUser()
        val remoteUser = userRepository.findById(loginUser!!.id)

        _viewState.value = ProfileViewState(user = (remoteUser as Result.Success).data)
    }

    fun logOut(context: Context) = viewModelScope.launch {
        _isLoading.value = true
        val result = authRepository.logOut(context)

        _isLoading.value = false
        if (result is Result.Error) {
            Log.e(TAG, "logOut: ${result.exception.message}", result.exception)
            _errorMessage.value = Event(result.exception.message.toString())
            return@launch
        }

        Preferences.userId = ""
        _isLoggedOut.value = Event(true)
    }

}