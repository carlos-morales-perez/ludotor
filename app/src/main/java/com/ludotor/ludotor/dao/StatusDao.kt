package com.ludotor.ludotor.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ludotor.ludotor.data.Status

@Dao
interface StatusDao {

    @Query("SELECT * FROM status WHERE boardGameId = :boardGameId ORDER BY statusId DESC")
    fun getStatusesForBoardGame(boardGameId: Int): Status

    @Query("SELECT * FROM status WHERE statusId = :statusId")
    fun getStatusById(statusId: Int): LiveData<Status?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatus(status: Status)

    @Update
    suspend fun updateStatus(status: Status)

    @Delete
    suspend fun deleteStatus(status: Status)

    @Query("DELETE FROM status WHERE boardGameId = :boardGameId")
    suspend fun deleteStatusesByBoardGameId(boardGameId: Int)

}