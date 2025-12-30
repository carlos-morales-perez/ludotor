package com.ludotor.ludotor.repository
import androidx.lifecycle.LiveData
import com.ludotor.ludotor.dao.LoanDao
import com.ludotor.ludotor.data.Loan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class LoanRepository(private val loanDao: LoanDao) {

    fun getLoansForBoardGame(boardGameId: Int): LiveData<List<Loan>> {
        return loanDao.getLoansForBoardGame(boardGameId)
    }

    val activeLoans: LiveData<List<Loan>> = loanDao.getActiveLoans() // Asume que loanReturnDate es null o una fecha futura para activos

    val allLoans: LiveData<List<Loan>> = loanDao.getAllLoans()

    fun getLoanById(loanId: Int): LiveData<Loan?> {
        return loanDao.getLoanById(loanId)
    }

    suspend fun insert(loan: Loan): Long {
        return loanDao.insertLoan(loan)
    }

    suspend fun update(loan: Loan) {
        withContext(Dispatchers.IO) {
            loanDao.updateLoan(loan)
        }
    }

    suspend fun delete(loan: Loan) {
        withContext(Dispatchers.IO) {
            loanDao.deleteLoan(loan)
        }
    }
}