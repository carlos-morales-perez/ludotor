package com.ludotor.ludotor.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ludotor.ludotor.data.BoardGame
import com.ludotor.ludotor.data.Notification
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notification ORDER BY notificationId DESC")
    fun getAllNotifications(): LiveData<List<Notification>>

    @Query("SELECT * FROM notification WHERE notificationId = :notificationId")
    fun getNotificationById(notificationId: Int): LiveData<Notification?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification): Long // Devolver Long (rowId) puede ser útil

    @Update
    suspend fun updateNotification(notification: Notification)

    @Delete
    suspend fun deleteNotification(notification: Notification)

    @Query("DELETE FROM notification")
    suspend fun deleteAllNotifications()

}