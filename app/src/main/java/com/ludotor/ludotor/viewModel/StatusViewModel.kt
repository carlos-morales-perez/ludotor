package com.ludotor.ludotor.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ludotor.ludotor.data.AppDatabase
import com.ludotor.ludotor.data.Status
import com.ludotor.ludotor.repository.StatusRepository
import kotlinx.coroutines.launch

class StatusViewModel(application: Application) : AndroidViewModel(application) { // O ViewModel si no necesitas Application context

    private val repository: StatusRepository

    init {
        val statusDao = AppDatabase.getDatabase(application, viewModelScope).statusDao() // Ajusta según tu inicialización de BD
        repository = StatusRepository(statusDao)

    }

    fun insertStatus(status: Status) = viewModelScope.launch {
        repository.insert(status)
    }

//     fun getStatusesForGame(boardGameId: Int): LiveData<List<Status>> {
//         return repository.getStatusesForBoardGame(boardGameId)
//     }
}