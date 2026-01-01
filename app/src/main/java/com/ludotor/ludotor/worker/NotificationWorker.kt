package com.ludotor.ludotor.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ludotor.ludotor.R
import com.ludotor.ludotor.data.AppDatabase
import java.sql.Date
import java.util.Calendar

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        const val CHANNEL_ID = "LOAN_REMINDERS_CHANNEL"
        const val NOTIFICATION_ID_BASE = 1000
    }

    override suspend fun doWork(): Result {
        return try {
            val sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)
            if (!notificationsEnabled) {
                return Result.success()
            }

            val notifcationDao = AppDatabase.getDatabase(applicationContext).notificationDao()

            val todayCalendar = Calendar.getInstance()
            val yesterdayCalendar = Calendar.getInstance()
            yesterdayCalendar.add(Calendar.DAY_OF_MONTH, -1)

            val today = Date(todayCalendar.timeInMillis)
            val yesterday = Date(yesterdayCalendar.timeInMillis)

            val notifications = notifcationDao.getNotificationByTargetDate(yesterday, today)

            if (notifications.isNotEmpty()) {
                notifications.forEachIndexed { index, notification ->
                    sendNotification(
                        applicationContext, "Préstamo vence hoy",
                        notification?.message ?: "Préstamo vence hoy",
                        NOTIFICATION_ID_BASE + (notification?.notificationId ?: 1)
                    )
                }
            }
            Log.d("NotificationWorker", "Notificaciones enviadas")
            Result.success()
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Error en doWork", e)
            Result.failure()
        }
    }

    private fun sendNotification(context: Context, title: String, message: String, notificationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Recordatorios de Préstamos",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Canal para recordatorios de vencimiento de préstamos"
        }
        notificationManager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, builder.build())
    }
}