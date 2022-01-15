package com.charuniverse.curious.ui.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.curious.data.exception
import com.charuniverse.curious.data.failed
import com.charuniverse.curious.data.repository.AuthRepository
import com.charuniverse.curious.data.repository.UserRepository
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

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    private val _loginSuccess = MutableLiveData<Event<Unit>>()
    val loginSuccess: LiveData<Event<Unit>> = _loginSuccess

    init {
        if (authRepository.getLoginUser() != null) {
            _loginSuccess.value = Event(Unit)
        }
    }

    fun buildGoogleSignInClient(context: Context): GoogleSignInClient {
        return authRepository.buildGoogleSignInClient(context)
    }

    fun loginWithGoogle(accountIdToken: String) = viewModelScope.launch {
        _loading.value = true

        val loginResult = authRepository.loginWithGoogle(accountIdToken)
        if (loginResult.failed) {
            _message.value = Event(loginResult.exception.message.toString())
            _loading.value = false
            return@launch
        }

        val loginUser = authRepository.getLoginUser()!!
        Log.i(TAG, "login user: $loginUser")

        val saveResult = userRepository.saveIfNotFound(loginUser)
        if (saveResult.failed) {
            _message.value = Event(saveResult.exception.message.toString())
            _loading.value = false
            return@launch
        }

        Preferences.userId = loginUser.id
        _loading.value = false
        _loginSuccess.value = Event(Unit)
    }

}