package com.example.pokemonapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pokemonapp.R
import com.example.pokemonapp.data.model.Pokemon
import com.example.pokemonapp.databinding.ItemPokemonBinding

class PokemonAdapter(
    private var pokemonList: MutableList<Pokemon>,
    private val onPokeClick: (Pokemon) -> Unit,
    private val onEditClick: (Pokemon) -> Unit,
    private val onDeleteClick: (Pokemon) -> Unit
) : RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    inner class PokemonViewHolder(private val binding: ItemPokemonBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pokemon: Pokemon) {
            binding.pokemonName.text = pokemon.name.capitalize()
            Glide.with(binding.root)
                .load(pokemon.imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(binding.pokemonImage)

            binding.root.setOnClickListener { onPokeClick(pokemon) }
            binding.editPokeBtn.setOnClickListener { onEditClick(pokemon) }
            binding.deletePokeBtn.setOnClickListener { onDeleteClick(pokemon) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val binding = ItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        holder.bind(pokemonList[position])
    }

    override fun getItemCount(): Int = pokemonList.size

    fun addPokemons(newPokemons: List<Pokemon>) {
        pokemonList = newPokemons.toMutableList()
        notifyDataSetChanged()
    }
}
