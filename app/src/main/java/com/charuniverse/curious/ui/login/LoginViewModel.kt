package com.charuniverse.curious.ui.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.source.AuthRepository
import com.charuniverse.curious.data.source.NotificationRepository
import com.charuniverse.curious.data.source.UserRepository
import com.charuniverse.curious.util.Event
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository,
) : ViewModel() {

    companion object {
        private const val TAG = "LoginViewModel"
    }

    private val _viewState = MutableLiveData<Event<LoginViewState>>()
    val viewState: LiveData<Event<LoginViewState>> = _viewState

    private fun updateViewState(
        isLoading: Boolean = false,
        error: Exception? = null,
        isLoggedIn: Boolean = false
    ) {
        _viewState.value = Event(
            LoginViewState(
                isLoading = isLoading,
                error = error,
                isLoggedIn = isLoggedIn
            )
        )
    }

    init {
        authRepository.getLoginUser()?.let {
            updateViewState(isLoggedIn = true)
        }
    }

    fun buildGoogleSignInClient(context: Context): GoogleSignInClient {
        return authRepository.buildGoogleSignInClient(context)
    }

    fun loginWithGoogle(accountIdToken: String) = viewModelScope.launch {
        authRepository.loginWithGoogle(accountIdToken).collect { res ->
            resultHandler(res) { updateViewState(isLoggedIn = true) }
        }
    }

    private fun <T> resultHandler(result: Result<T>, onSuccess: (T) -> Unit) {
        when (result) {
            is Result.Loading -> updateViewState(isLoading = true)
            is Result.Success -> onSuccess(result.data)
            is Result.Error -> {
                Log.e(TAG, "resultHandler: ${result.exception.message}", result.exception)
                updateViewState(error = result.exception)
            }
        }
    }

}