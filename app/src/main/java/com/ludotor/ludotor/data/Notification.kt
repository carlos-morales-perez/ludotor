package com.ludotor.ludotor.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "notification",
    foreignKeys = [
        ForeignKey(
            entity = BoardGame::class,
            parentColumns = ["boardGameId"],
            childColumns = ["boardGameId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Loan::class,
            parentColumns = ["loanId"],
            childColumns = ["loanId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)

data class Notification(
    @PrimaryKey(autoGenerate = true) val notificationId: Int = 0,
    val boardGameId : Int,
    val loanId: Int,
    val message: String,
    val targetDate: Date

)