package com.ludotor.ludotor.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ludotor.ludotor.data.BoardGame
import com.ludotor.ludotor.data.BoardGameWithDetails

@Dao
interface BoardGameDao {

    @Query("SELECT * FROM board_games ORDER BY name ASC")
    fun getAll(): LiveData<List<BoardGame>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(game: BoardGame)

    @Query("SELECT * FROM board_games WHERE boardGameId = :id")
    suspend fun getById(id: Int): BoardGame

    @Update
    suspend fun update(game: BoardGame)

    @Delete
    suspend fun delete(game: BoardGame)

    @Query("DELETE FROM board_games WHERE boardGameId = :gameId")
    suspend fun deleteGameById(gameId: Int): Int

    @Transaction
    @Query("SELECT * FROM board_games WHERE boardGameId = :gameId")
    fun getBoardGameWithDetailsById(gameId: Int): LiveData<BoardGameWithDetails?>
}