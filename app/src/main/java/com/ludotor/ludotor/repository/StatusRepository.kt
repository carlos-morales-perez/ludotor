package com.ludotor.ludotor.repository

import androidx.lifecycle.LiveData
import com.ludotor.ludotor.dao.StatusDao
import com.ludotor.ludotor.data.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StatusRepository(private val statusDao: StatusDao) {

    fun getStatusesForBoardGame(boardGameId: Int): Status {
        return statusDao.getStatusesForBoardGame(boardGameId)
    }

    fun getStatusById(statusId: Int): LiveData<Status?> {
        return statusDao.getStatusById(statusId)
    }

    suspend fun insert(status: Status) {
        withContext(Dispatchers.IO) { // Asegura que la operación se haga en un hilo de I/O
            statusDao.insertStatus(status)
        }
    }

    suspend fun update(status: Status) {
        withContext(Dispatchers.IO) {
            statusDao.updateStatus(status)
        }
    }

    suspend fun delete(status: Status) {
        withContext(Dispatchers.IO) {
            statusDao.deleteStatus(status)
        }
    }

    suspend fun deleteStatusesForBoardGame(boardGameId: Int) {
        withContext(Dispatchers.IO) {
            statusDao.deleteStatusesByBoardGameId(boardGameId)
        }
    }
}
