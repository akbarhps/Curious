package com.charuniverse.curious.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.repository.AuthRepository
import com.charuniverse.curious.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _viewState = MutableLiveData<ProfileViewState>()
    val viewState: LiveData<ProfileViewState> = _viewState

    init {
        getUser()
    }

    private fun getUser() = viewModelScope.launch {
        _viewState.value = ProfileViewState(isLoading = true)

        val loginUser = authRepository.getLoginUser()
        val remoteUser = userRepository.findById(loginUser!!.id)

        _viewState.value = ProfileViewState(user = (remoteUser as Result.Success).data)
    }

}