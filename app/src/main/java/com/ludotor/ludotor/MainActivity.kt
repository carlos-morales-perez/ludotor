package com.ludotor.ludotor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ludotor.ludotor.databinding.ActivityMainBinding
import androidx.activity.viewModels
import com.ludotor.ludotor.activity.EditGameActivity
import com.ludotor.ludotor.activity.GameDetailActivity
import com.ludotor.ludotor.adapter.BoardGameAdapter
import com.ludotor.ludotor.viewModel.BoardGameViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BoardGameAdapter
    private lateinit var binding: ActivityMainBinding
    private val viewModel: BoardGameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        adapter = BoardGameAdapter(emptyList()) { game ->
            val intent = Intent(this, GameDetailActivity::class.java).apply {
                putExtra("game_id", game.boardGameId)
            }
            startActivity(intent)
        }

        binding.rvBoardGames.layoutManager = LinearLayoutManager(this)
        binding.rvBoardGames.adapter = adapter

        viewModel.allGames.observe(this) { gameList ->
            Log.d("MainActivity", "allGames LiveData updated. List size: ${gameList.size}")
            adapter.updateList(gameList)
        }

        recyclerView = findViewById(R.id.rv_board_games)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val fab: FloatingActionButton = findViewById(R.id.fab_add_game)
        fab.setOnClickListener {
            val intent = Intent(this, EditGameActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.drawer_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // Acción de configuraciones
                true
            }
            R.id.action_about -> {
                // Acción de acerca de
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
    }
}
