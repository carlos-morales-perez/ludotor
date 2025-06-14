package com.ludotor.ludotor.repository

import androidx.lifecycle.LiveData
import com.ludotor.ludotor.dao.StatusDao
import com.ludotor.ludotor.data.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository module for handling data operations for Status.
 */
class StatusRepository(private val statusDao: StatusDao) {

    /**
     * Retrieves all statuses for a specific board game as LiveData.
     * @param boardGameId The ID of the board game.
     * @return LiveData list of Status objects.
     */
    fun getStatusesForBoardGame(boardGameId: Int): Status {
        return statusDao.getStatusesForBoardGame(boardGameId)
    }

    /**
     * Retrieves a specific status by its ID as LiveData.
     * @param statusId The ID of the status.
     * @return LiveData Status object, or null if not found.
     */
    fun getStatusById(statusId: Int): LiveData<Status?> {
        return statusDao.getStatusById(statusId)
    }

    /**
     * Inserts a new status into the database.
     * This operation is performed on a background thread.
     * @param status The Status object to insert.
     */
    suspend fun insert(status: Status) {
        withContext(Dispatchers.IO) { // Asegura que la operación se haga en un hilo de I/O
            statusDao.insertStatus(status)
        }
    }

    /**
     * Updates an existing status in the database.
     * This operation is performed on a background thread.
     * @param status The Status object to update.
     */
    suspend fun update(status: Status) {
        withContext(Dispatchers.IO) {
            statusDao.updateStatus(status)
        }
    }

    /**
     * Deletes a status from the database.
     * This operation is performed on a background thread.
     * @param status The Status object to delete.
     */
    suspend fun delete(status: Status) {
        withContext(Dispatchers.IO) {
            statusDao.deleteStatus(status)
        }
    }

    /**
     * Deletes all statuses associated with a specific board game ID.
     * This operation is performed on a background thread.
     * @param boardGameId The ID of the board game whose statuses will be deleted.
     */
    suspend fun deleteStatusesForBoardGame(boardGameId: Int) {
        withContext(Dispatchers.IO) {
            statusDao.deleteStatusesByBoardGameId(boardGameId)
        }
    }
}
