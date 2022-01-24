package com.charuniverse.curious.ui.notification

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.dto.NotificationDetail
import com.charuniverse.curious.data.source.NotificationRepository
import com.charuniverse.curious.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    companion object {
        private const val TAG = "VMNotification"
    }

    private val _viewState = MutableLiveData<Event<NotificationViewState>>()
    val viewState: LiveData<Event<NotificationViewState>> = _viewState

    private fun updateViewState(
        isLoading: Boolean = false,
        error: Exception? = null,
        selectedNotificationId: String? = null,
    ) {
        _viewState.value = Event(
            NotificationViewState(
                isLoading = isLoading,
                error = error,
                selectedNotificationId = selectedNotificationId,
            )
        )
    }

    private val _notifications = MutableLiveData<List<NotificationDetail>>()
    val notifications: LiveData<List<NotificationDetail>> = _notifications

    init {
        refresh(false)
    }

    fun refresh(forceRefresh: Boolean = false) = viewModelScope.launch {
        notificationRepository.getByUserId(forceRefresh).collect { res ->
            resultHandler(res) {
                updateViewState(isLoading = false)
                _notifications.value = it
            }
        }
    }

    fun setSelectedNotificationId(id: String) {
        updateViewState(selectedNotificationId = id)
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