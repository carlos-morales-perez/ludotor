package com.ludotor.ludotor.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "status",
    foreignKeys = [
        ForeignKey(
            entity = BoardGame::class,
            parentColumns = ["boardGameId"],
            childColumns = ["boardGameId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class Status (
    @PrimaryKey(autoGenerate = true) val statusId: Int = 0,
    val boardGameId: Int,
    val sleeves: Boolean,
    val condition: String,
    val dateOfIncorporation: Date?

)