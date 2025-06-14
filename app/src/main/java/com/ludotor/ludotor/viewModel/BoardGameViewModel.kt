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

    // 1. Repositorio: El ViewModel interactúa con el repositorio para obtener/guardar datos.
    private val repository: BoardGameRepository

    // 2. LiveData para exponer la lista de juegos a la UI (MainActivity).
    // Room puede devolver LiveData directamente, que se actualiza automáticamente.
    val allGames: LiveData<List<BoardGame>>

    // 3. LiveData para exponer un juego específico a la UI (GameDetailActivity).
    // Usamos MutableLiveData aquí si necesitamos obtener el juego bajo demanda.
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

    /**
     * Obtiene un juego por su ID y actualiza _currentGame LiveData.
     * Usado por GameDetailActivity.
     */
    fun getGameById(gameId: Int) {
        viewModelScope.launch {
            // Si el ID es 0 o -1 (indicando un nuevo juego), no cargues nada o establece un juego vacío.
            if (gameId <= 0) {
                _currentGame.postValue(null) // O un BoardGame() vacío si prefieres
            } else {
                _currentGame.postValue(repository.getGameById(gameId))
            }
        }
    }

    fun loadGameById(gameId: Int) {
        _gameId.value = gameId // Esto disparará la transformación switchMap
    }

    /**
     * Guarda un juego (inserta si es nuevo, actualiza si ya existe).
     * Usado por GameDetailActivity.
     */
    fun saveGame(game: BoardGame) {
        viewModelScope.launch {
            if (game.boardGameId == 0) { // Asumiendo que 0 indica un nuevo juego
                repository.insert(game)
            } else {
                repository.update(game)
            }
        }
    }

    /**
     * Elimina un juego.
     * Podría ser usado por MainActivity.
     */
    fun deleteGame(game: BoardGame) {
        viewModelScope.launch {
            repository.delete(game)
        }
    }

    /**
     * Opcional: Si necesitas limpiar algo específico cuando el ViewModel está a punto de ser destruido.
     * LiveData se limpia automáticamente.
     */
    override fun onCleared() {
        super.onCleared()
        // Limpiar recursos si es necesario, por ejemplo, cancelar coroutines de larga duración si no están en viewModelScope.
    }

    fun deleteGameById(gameId: Int) = viewModelScope.launch {
        if (gameId != 0) { // Asegúrate de que el ID es válido
            repository.deleteGameById(gameId) // Llama al método del repositorio
            // También podrías querer borrar datos relacionados, como Status o Loans
            // statusRepository.deleteStatusesForBoardGame(gameId)
            // loanRepository.deleteLoansForBoardGame(gameId)
        }
    }
}