package com.ludotor.ludotor.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ludotor.ludotor.data.AppDatabase
import com.ludotor.ludotor.data.Notification
import com.ludotor.ludotor.repository.NotificationRepository
import kotlinx.coroutines.launch

class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val notificationRepository: NotificationRepository

    init {
        val notificationDao = AppDatabase.getDatabase(application).notificationDao()
        notificationRepository = NotificationRepository(notificationDao)
    }

    fun insertNotification(notification: Notification) = viewModelScope.launch {
        notificationRepository.insert(notification)
    }

}