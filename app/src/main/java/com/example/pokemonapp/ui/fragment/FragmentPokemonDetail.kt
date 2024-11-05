package com.example.pokemonapp.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.pokemonapp.R
import com.example.pokemonapp.data.model.Pokemon
import com.example.pokemonapp.data.model.PokemonDetails
import com.example.pokemonapp.ui.viewmodel.PokemonProfileViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class FragmentPokemonDetail : Fragment() {
    private val args: FragmentPokemonDetailArgs by navArgs()
    private val viewModel: PokemonProfileViewModel by viewModels()

    private lateinit var pokemonImage: ImageView
    private lateinit var pokemonName: TextView
    private lateinit var abilityChipGroup: ChipGroup
    private lateinit var moveChipGroup: ChipGroup
    private lateinit var pokemonStats: LinearLayout
    private lateinit var pokemonTypes: ChipGroup
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pokemon_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pokemonImage = view.findViewById(R.id.pokemon_image)
        pokemonName = view.findViewById(R.id.pokemon_name)
        abilityChipGroup = view.findViewById(R.id.ability_chip_group)
        moveChipGroup = view.findViewById(R.id.move_list)
        pokemonStats = view.findViewById(R.id.pokemon_stats)
        pokemonTypes = view.findViewById(R.id.pokemon_types)
        progressBar = view.findViewById(R.id.progressBar)

        val pokemon: Pokemon = args.pokemon

        Glide.with(this).load(pokemon.imageUrl).into(pokemonImage)
        pokemonName.text = pokemon.name

        progressBar.visibility = View.VISIBLE

        viewModel.getPokemonDetails(pokemon.id).observe(viewLifecycleOwner) { pokemonDetails ->
            if (pokemonDetails != null) {
                displayPokemonDetails(pokemonDetails)

                progressBar.visibility = View.GONE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayPokemonDetails(pokemonDetails: PokemonDetails) {
        pokemonDetails.abilities.take(10).forEach { ability ->
            val chip = Chip(requireContext()).apply {
                text = ability.ability.name
                isCheckable = false
            }
            abilityChipGroup.addView(chip)
        }
        abilityChipGroup.visibility = View.VISIBLE

        pokemonDetails.moves.take(10).forEach { move ->
            val chip = Chip(requireContext()).apply {
                text = move.move.name
                isCheckable = false
            }
            moveChipGroup.addView(chip)
        }
        moveChipGroup.visibility = View.VISIBLE

        pokemonDetails.stats.forEach { stat ->
            val text = TextView(requireContext()).apply {
                text = "${stat.stat.name} : ${stat.base_stat} Effort: ${stat.effort}"
            }
            pokemonStats.addView(text)
        }
        pokemonStats.visibility = View.VISIBLE

        pokemonDetails.types.forEach { type ->
            val chip = Chip(requireContext()).apply {
                text = type.type.name
                isCheckable = false
            }
            pokemonTypes.addView(chip)
        }
        pokemonTypes.visibility = View.VISIBLE
    }
}