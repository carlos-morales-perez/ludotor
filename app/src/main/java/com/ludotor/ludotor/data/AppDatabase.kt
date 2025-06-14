package com.ludotor.ludotor.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ludotor.ludotor.dao.BoardGameDao
import com.ludotor.ludotor.dao.LoanDao
import com.ludotor.ludotor.dao.NotificationDao
import com.ludotor.ludotor.dao.StatusDao
import com.ludotor.ludotor.utils.Converters
import kotlinx.coroutines.CoroutineScope

@Database(entities = [BoardGame::class, Notification::class, Status::class, Loan::class], version = 9)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun boardGameDao(): BoardGameDao
    abstract fun notificationDao(): NotificationDao
    abstract fun statusDao(): StatusDao
    abstract fun loanDao(): LoanDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, viewModelScope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "board_game_db"
                ).fallbackToDestructiveMigration()
                    .setJournalMode(JournalMode.TRUNCATE)
                    .build().also { INSTANCE = it }
            }
        }
    }
}