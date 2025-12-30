package com.ludotor.ludotor.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.ludotor.ludotor.R
import com.ludotor.ludotor.data.BoardGame
import com.ludotor.ludotor.databinding.ItemBoardGameBinding

class BoardGameAdapter(
    private var games: List<BoardGame>,
    private var onItemClicked: (BoardGame) -> Unit)
        : RecyclerView.Adapter<BoardGameAdapter.GameViewHolder>() {

    inner class GameViewHolder(val binding: ItemBoardGameBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        Log.d("BoardGameAdapter", "onCreateViewHolder called")
        val binding = ItemBoardGameBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return GameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        holder.binding.apply {
            tvGameName.text = game.name
            tvPlayers.text = "Jugadores: ${game.playerMin} - ${game.playerMax} jugadores"
            tvPlayTime.text = "Duración: ${game.playTime} minutos"
            if (game.gameImage.isNotEmpty()) {
                imgGamePhoto.setImageURI(game.gameImage.toUri())
            } else {
                imgGamePhoto.setImageResource(R.drawable.ic_games)
            }

            root.setOnClickListener {
                onItemClicked(game)
            }
        }
    }

    fun updateList(newGameList: List<BoardGame>) {
        games = newGameList
        notifyDataSetChanged()
        Log.d("BoardGameAdapter", "List updated in adapter. New size: ${newGameList.size}")
    }

    override fun getItemCount(): Int {
        Log.d("BoardGameAdapter", "getItemCount() called. Size: ${games.size}")
        return games.size
    }

}