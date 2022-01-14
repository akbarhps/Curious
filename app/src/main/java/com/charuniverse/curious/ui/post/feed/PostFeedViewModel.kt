package com.charuniverse.curious.ui.post.feed

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.curious.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostFeedViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun logot(context: Context) = viewModelScope.launch {
        authRepository.logOut(context)
    }
}