package com.ludotor.ludotor.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ludotor.ludotor.data.Loan

@Dao
interface LoanDao {

    @Query("SELECT * FROM loan WHERE boardGameId = :boardGameId ORDER BY loanDate DESC")
    fun getLoansForBoardGame(boardGameId: Int): LiveData<List<Loan>>

    @Query("SELECT * FROM loan WHERE loanId = :loanId")
    fun getLoanById(loanId: Int): LiveData<Loan?>

    @Query("SELECT * FROM loan WHERE targetDate IS NULL OR targetDate = '' ORDER BY loanDate DESC")
    fun getActiveLoans(): LiveData<List<Loan>>

    @Query("SELECT * FROM loan ORDER BY loanDate DESC")
    fun getAllLoans(): LiveData<List<Loan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: Loan)

    @Update
    suspend fun updateLoan(loan: Loan)

    @Delete
    suspend fun deleteLoan(loan: Loan)

}