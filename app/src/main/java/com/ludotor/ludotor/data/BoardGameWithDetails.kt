package com.ludotor.ludotor.data

import androidx.room.Embedded
import androidx.room.Relation

data class BoardGameWithDetails(
    @Embedded
    val boardGame: BoardGame,

    @Relation(
        parentColumn = "boardGameId",
        entityColumn = "boardGameId"
    )
    val status: Status?,

    @Relation(
        parentColumn = "boardGameId",
        entityColumn = "boardGameId"
    )
    val loans: List<Loan>
)
