package com.ludotor.ludotor.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "board_games")
data class BoardGame(
    @PrimaryKey(autoGenerate = true) val boardGameId: Int = 0,
    val name: String,
    val playerMin: Int,
    val playerMax: Int,
    val playTime: String,
    val gameImage: String = ""
)