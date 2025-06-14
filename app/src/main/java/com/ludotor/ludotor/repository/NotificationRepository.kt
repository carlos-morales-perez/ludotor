package com.ludotor.ludotor.repository

import androidx.lifecycle.LiveData
import com.ludotor.ludotor.dao.NotificationDao
import com.ludotor.ludotor.data.Notification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationRepository(private val notificationDao: NotificationDao) {

    /**
     * Retrieves all notifications, typically ordered by date.
     * @return LiveData list of Notification objects.
     */
    val allNotifications: LiveData<List<Notification>> = notificationDao.getAllNotifications()

    /**
     * Retrieves a specific notification by its ID.
     * @param notificationId The ID of the notification.
     * @return LiveData Notification object, or null if not found.
     */
    fun getNotificationById(notificationId: Int): LiveData<Notification?> {
        return notificationDao.getNotificationById(notificationId)
    }

    /**
     * Inserts a new notification into the database.
     * This operation is performed on a background thread.
     * @param notification The Notification object to insert.
     * @return The row ID of the newly inserted notification.
     */
    suspend fun insert(notification: Notification): Long {
        return withContext(Dispatchers.IO) {
            notificationDao.insertNotification(notification)
        }
    }

    /**
     * Updates an existing notification in the database.
     * This operation is performed on a background thread.
     * @param notification The Notification object to update.
     */
    suspend fun update(notification: Notification) {
        withContext(Dispatchers.IO) {
            notificationDao.updateNotification(notification)
        }
    }

    /**
     * Deletes a notification from the database.
     * This operation is performed on a background thread.
     * @param notification The Notification object to delete.
     */
    suspend fun delete(notification: Notification) {
        withContext(Dispatchers.IO) {
            notificationDao.deleteNotification(notification)
        }
    }

    /**
     * Deletes all notifications from the database.
     * This operation is performed on a background thread.
     */
    suspend fun deleteAllNotifications() {
        withContext(Dispatchers.IO) {
            notificationDao.deleteAllNotifications()
        }
    }
}