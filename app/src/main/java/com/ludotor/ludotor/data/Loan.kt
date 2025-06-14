package com.ludotor.ludotor.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "loan",
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

data class Loan(
    @PrimaryKey(autoGenerate = true) val loanId: Int = 0,
    val boardGameId: Int,
    val borrower: String,
    val loanDate: Date,
    val targetDate: Date
)
