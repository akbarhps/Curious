package com.charuniverse.curious.ui.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.repository.AuthRepository
import com.charuniverse.curious.data.repository.UserRepository
import com.charuniverse.curious.ui.post.BaseViewState
import com.charuniverse.curious.util.Event
import com.charuniverse.curious.util.Preferences
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    companion object {
        private const val TAG = "LoginViewModel"
    }

    private val _viewState = MutableLiveData<Event<BaseViewState>>()
    val viewState: LiveData<Event<BaseViewState>> = _viewState

    private fun updateViewState(
        isLoading: Boolean = false, error: Exception? = null, isCompleted: Boolean = false
    ) {
        _viewState.value =
            Event(BaseViewState(isLoading = isLoading, error = error, isCompleted = isCompleted))
    }

    init {
        authRepository.getLoginUser()?.let {
            updateViewState(isCompleted = true)
        }
    }

    fun buildGoogleSignInClient(context: Context): GoogleSignInClient {
        return authRepository.buildGoogleSignInClient(context)
    }

    fun loginWithGoogle(accountIdToken: String) = viewModelScope.launch {
        updateViewState(isLoading = true)

        val loginResult = authRepository.loginWithGoogle(accountIdToken)
        if (loginResult is Result.Error) {
            updateViewState(error = loginResult.exception)
            return@launch
        }

        val loginUser = authRepository.getLoginUser()!!
        val saveResult = userRepository.saveIfNotExist(loginUser)
        if (saveResult is Result.Error) {
            updateViewState(error = saveResult.exception)
            return@launch
        }

        Preferences.userId = loginUser.id
        updateViewState(isCompleted = true)
    }

}