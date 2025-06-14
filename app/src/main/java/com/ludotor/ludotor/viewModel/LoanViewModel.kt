package com.ludotor.ludotor.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ludotor.ludotor.data.AppDatabase
import com.ludotor.ludotor.data.Loan
import com.ludotor.ludotor.repository.LoanRepository
import kotlinx.coroutines.launch

class LoanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LoanRepository

    init {
        val loanDao = AppDatabase.getDatabase(application, viewModelScope).loanDao()
        repository = LoanRepository(loanDao)
    }

    fun insertLoan(loan: Loan) = viewModelScope.launch {
        repository.insert(loan)
    }

    suspend fun insertLoanAndGetId(loan: Loan): Long {
        return repository.insert(loan)
    }

    fun getAllLoansForBoardGame(boardGameId: Int) = repository.getLoansForBoardGame(boardGameId)

}