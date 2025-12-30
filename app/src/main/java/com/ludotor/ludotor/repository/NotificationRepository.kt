package com.ludotor.ludotor.repository

import androidx.lifecycle.LiveData
import com.ludotor.ludotor.dao.NotificationDao
import com.ludotor.ludotor.data.Notification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationRepository(private val notificationDao: NotificationDao) {

    val allNotifications: LiveData<List<Notification>> = notificationDao.getAllNotifications()

    fun getNotificationById(notificationId: Int): LiveData<Notification?> {
        return notificationDao.getNotificationById(notificationId)
    }

    suspend fun insert(notification: Notification) {
        withContext(Dispatchers.IO) {
            notificationDao.insertNotification(notification)
        }
    }

    suspend fun update(notification: Notification) {
        withContext(Dispatchers.IO) {
            notificationDao.updateNotification(notification)
        }
    }

    suspend fun delete(notification: Notification) {
        withContext(Dispatchers.IO) {
            notificationDao.deleteNotification(notification)
        }
    }

    suspend fun deleteAllNotifications() {
        withContext(Dispatchers.IO) {
            notificationDao.deleteAllNotifications()
        }
    }
}