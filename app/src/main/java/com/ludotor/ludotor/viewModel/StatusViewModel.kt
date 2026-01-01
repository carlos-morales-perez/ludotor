package com.ludotor.ludotor.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ludotor.ludotor.data.AppDatabase
import com.ludotor.ludotor.data.Status
import com.ludotor.ludotor.repository.StatusRepository
import kotlinx.coroutines.launch

class StatusViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StatusRepository

    init {
        val statusDao = AppDatabase.getDatabase(application).statusDao()
        repository = StatusRepository(statusDao)

    }

    fun insertStatus(status: Status) = viewModelScope.launch {
        repository.insert(status)
    }

}