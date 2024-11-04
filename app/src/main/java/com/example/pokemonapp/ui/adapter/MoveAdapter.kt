package com.example.pokemonapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pokemonapp.R
import com.example.pokemonapp.data.model.Move


class MoveAdapter(private val moves: List<Move>) : RecyclerView.Adapter<MoveAdapter.MoveViewHolder>() {

    inner class MoveViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val moveName: TextView = view.findViewById(R.id.move_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoveViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_move, parent, false)
        return MoveViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoveViewHolder, position: Int) {
        holder.moveName.text = moves[position].name
    }

    override fun getItemCount(): Int = moves.size
}
