package com.ludotor.ludotor.repository

import androidx.lifecycle.LiveData
import com.ludotor.ludotor.dao.BoardGameDao
import com.ludotor.ludotor.data.BoardGame
import com.ludotor.ludotor.data.BoardGameWithDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BoardGameRepository(private val boardGameDao: BoardGameDao) {

    val allGames: LiveData<List<BoardGame>> = boardGameDao.getAll()

    suspend fun getGameById(id: Int): BoardGame {
        return boardGameDao.getById(id)
    }

    suspend fun insert(boardGame: BoardGame) {
        boardGameDao.insert(boardGame)
    }

    suspend fun update(boardGame: BoardGame) {
        boardGameDao.update(boardGame)
    }

    suspend fun delete(boardGame: BoardGame) {
        boardGameDao.delete(boardGame)
    }

    suspend fun deleteGameById(gameId: Int) {
        withContext(Dispatchers.IO) {
            boardGameDao.deleteGameById(gameId)
        }
    }

    fun getBoardGameWithDetails(gameId: Int): LiveData<BoardGameWithDetails?> {
        return boardGameDao.getBoardGameWithDetailsById(gameId)
    }
}