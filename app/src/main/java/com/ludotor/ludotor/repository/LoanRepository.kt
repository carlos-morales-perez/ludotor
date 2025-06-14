package com.ludotor.ludotor.repository
import androidx.lifecycle.LiveData
import com.ludotor.ludotor.dao.LoanDao
import com.ludotor.ludotor.data.Loan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository module for handling data operations for Loan.
 */
class LoanRepository(private val loanDao: LoanDao) {

    /**
     * Retrieves all loans for a specific board game as LiveData.
     * @param boardGameId The ID of the board game.
     * @return LiveData list of Loan objects.
     */
    fun getLoansForBoardGame(boardGameId: Int): LiveData<List<Loan>> {
        return loanDao.getLoansForBoardGame(boardGameId)
    }

    /**
     * Retrieves all active loans (not yet returned) as LiveData.
     * @return LiveData list of active Loan objects.
     */
    val activeLoans: LiveData<List<Loan>> = loanDao.getActiveLoans() // Asume que loanReturnDate es null o una fecha futura para activos

    /**
     * Retrieves all loans from the database as LiveData.
     * @return LiveData list of all Loan objects.
     */
    val allLoans: LiveData<List<Loan>> = loanDao.getAllLoans()


    /**
     * Retrieves a specific loan by its ID as LiveData.
     * @param loanId The ID of the loan.
     * @return LiveData Loan object, or null if not found.
     */
    fun getLoanById(loanId: Int): LiveData<Loan?> {
        return loanDao.getLoanById(loanId)
    }

    /**
     * Inserts a new loan into the database.
     * This operation is performed on a background thread.
     * @param loan The Loan object to insert.
     */
    suspend fun insert(loan: Loan): Long {
        return loanDao.insertLoan(loan)
    }

    /**
     * Updates an existing loan in the database.
     * This operation is performed on a background thread.
     * @param loan The Loan object to update.
     */
    suspend fun update(loan: Loan) {
        withContext(Dispatchers.IO) {
            loanDao.updateLoan(loan)
        }
    }

    /**
     * Deletes a loan from the database.
     * This operation is performed on a background thread.
     * @param loan The Loan object to delete.
     */
    suspend fun delete(loan: Loan) {
        withContext(Dispatchers.IO) {
            loanDao.deleteLoan(loan)
        }
    }
}