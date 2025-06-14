package com.ludotor.ludotor.activity

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ludotor.ludotor.viewModel.BoardGameViewModel
import com.ludotor.ludotor.R
import com.ludotor.ludotor.data.BoardGame

class EditGameActivity : AppCompatActivity() {

    private val viewModel: BoardGameViewModel by viewModels()

    private var currentGameId: Int = 0
    private var isNewGame: Boolean = true

    private lateinit var nameField: EditText
    private lateinit var minPlayersField: EditText
    private lateinit var maxPlayersField: EditText
    private lateinit var playTimeField: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_game)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_game_detail)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        nameField = findViewById(R.id.etGameName)
        minPlayersField = findViewById(R.id.etMinPlayers)
        maxPlayersField = findViewById(R.id.etMaxPlayers)
        playTimeField = findViewById(R.id.etPlayTime)
        btnSave = findViewById(R.id.btnSave)

        currentGameId = intent.getIntExtra("game_id", 0)
        isNewGame = (currentGameId == 0)

        if (!isNewGame) {
            title = "Editar Juego"
            viewModel.getGameById(currentGameId)
        } else {
            title = "Añadir Juego" // Opcional
        }

        viewModel.currentGame.observe(this) { game ->
            game?.let {
                if (!isNewGame) {
                    nameField.setText(it.name)
                    minPlayersField.setText(it.playerMin.toString())
                    maxPlayersField.setText(it.playerMax.toString())
                    playTimeField.setText(it.playTime)
                    // gameImageField.setText(it.gameImage)
                }
            }
        }

        btnSave.setOnClickListener {
            saveGameDetails()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveGameDetails() {
        var isEmpty = false
        val name = nameField.text.toString().trim()
        val minPlayersStr = minPlayersField.text.toString().trim()
        val maxPlayersStr = maxPlayersField.text.toString().trim()
        val playTime = playTimeField.text.toString().trim()
        // val gameImage = gameImageField.text.toString().trim() // Si tienes campo de imagen

        if (name.isEmpty()) {
            isEmpty = true
            nameField.error = "El nombre no puede estar vacío"
            nameField.requestFocus()
        }
        if (minPlayersStr.isEmpty()) {
            isEmpty = true
            minPlayersField.error = "Mín. de jugadores no puede estar vacío"
            minPlayersField.requestFocus()
        }
        if (maxPlayersStr.isEmpty()) {
            isEmpty = true
            maxPlayersField.error = "Máx. de jugadores no puede estar vacío"
            maxPlayersField.requestFocus()
        }
        if (playTime.isEmpty()) {
            isEmpty = true
            playTimeField.error = "La duración no puede estar vacía"
            playTimeField.requestFocus()
        }

        if (isEmpty) {
            Toast.makeText(this, "Hay errores en campos requeridos", Toast.LENGTH_SHORT).show()
            return
        }

        val minPlayers = minPlayersStr.toIntOrNull()
        val maxPlayers = maxPlayersStr.toIntOrNull()

        if (minPlayers == null || minPlayers <= 0) {
            minPlayersField.error = "Valor inválido para mín. de jugadores"
            minPlayersField.requestFocus()
            return
        }
        if (maxPlayers == null || maxPlayers < minPlayers) {
            maxPlayersField.error = "Máx. de jugadores debe ser mayor o igual al mínimo"
            maxPlayersField.requestFocus()
            return
        }

        val gameToSave = BoardGame(
            boardGameId = if (isNewGame) 0 else currentGameId,
            name = name,
            playerMin = minPlayers,
            playerMax = maxPlayers,
            playTime = playTime,
            gameImage = "" // O gameImage si lo estás manejando
        )

        viewModel.saveGame(gameToSave)
        setResult(RESULT_OK)
        Toast.makeText(this, "Juego guardado", Toast.LENGTH_SHORT).show()
        finish()
    }
}