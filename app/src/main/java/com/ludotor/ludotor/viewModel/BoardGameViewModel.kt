package com.ludotor.ludotor.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.ludotor.ludotor.data.BoardGame
import com.ludotor.ludotor.data.AppDatabase
import com.ludotor.ludotor.data.BoardGameWithDetails
import com.ludotor.ludotor.repository.BoardGameRepository
import kotlinx.coroutines.launch

class BoardGameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BoardGameRepository

    val allGames: LiveData<List<BoardGame>>

    private val _gameId = MutableLiveData<Int>()
    private val _currentGame = MutableLiveData<BoardGame?>()
    val currentGame: LiveData<BoardGame?> = _currentGame

    val currentGameWithDetails: LiveData<BoardGameWithDetails?> = _gameId.switchMap { id ->
        repository.getBoardGameWithDetails(id)
    }

    init {
        val boardGameDao = AppDatabase.getDatabase(application).boardGameDao()
        repository = BoardGameRepository(boardGameDao)
        allGames = repository.allGames
    }

    fun getGameById(gameId: Int) {
        viewModelScope.launch {

            if (gameId <= 0) {
                _currentGame.postValue(null)
            } else {
                _currentGame.postValue(repository.getGameById(gameId))
            }
        }
    }

    fun loadGameById(gameId: Int) {
        _gameId.value = gameId
    }

    fun saveGame(game: BoardGame) {
        viewModelScope.launch {
            if (game.boardGameId == 0) {
                repository.insert(game)
            } else {
                repository.update(game)
            }
        }
    }

    fun deleteGame(game: BoardGame) {
        viewModelScope.launch {
            repository.delete(game)
        }
    }


    override fun onCleared() {
        super.onCleared()
    }

    fun deleteGameById(gameId: Int) = viewModelScope.launch {
        if (gameId != 0) {
            repository.deleteGameById(gameId)

        }
    }
}