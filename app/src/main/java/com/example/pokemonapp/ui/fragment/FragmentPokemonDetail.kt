package com.example.pokemonapp.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.pokemonapp.data.model.Pokemon
import com.example.pokemonapp.data.model.PokemonDetails
import com.example.pokemonapp.databinding.FragmentPokemonDetailBinding
import com.example.pokemonapp.ui.viewmodel.PokemonProfileViewModel
import com.google.android.material.chip.Chip

class FragmentPokemonDetail : Fragment() {
    private var _binding: FragmentPokemonDetailBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("ViewBinding not initialized")

    private val args: FragmentPokemonDetailArgs by navArgs()
    private val viewModel: PokemonProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokemonDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pokemon: Pokemon = args.pokemon

        Glide.with(this).load(pokemon.imageUrl).into(binding.pokemonImage)
        binding.pokemonName.text = pokemon.name

        binding.progressBar.visibility = View.VISIBLE

        viewModel.getPokemonDetails(pokemon.id).observe(viewLifecycleOwner) { pokemonDetails ->
            if (pokemonDetails != null) {
                displayPokemonDetails(pokemonDetails)
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayPokemonDetails(pokemonDetails: PokemonDetails) {
        with(binding) {
            abilityChipGroup.apply {
                removeAllViews()
                pokemonDetails.abilities.take(10).forEach { ability ->
                    val chip = Chip(requireContext()).apply {
                        text = ability.ability.name
                        isCheckable = false
                    }
                    addView(chip)
                }
                visibility = View.VISIBLE
            }

            moveList.apply {
                removeAllViews()
                pokemonDetails.moves.take(10).forEach { move ->
                    val chip = Chip(requireContext()).apply {
                        text = move.move.name
                        isCheckable = false
                    }
                    addView(chip)
                }
                visibility = View.VISIBLE
            }

            pokemonStats.apply {
                removeAllViews()
                pokemonDetails.stats.forEach { stat ->
                    val text = TextView(requireContext()).apply {
                        text = "${stat.stat.name} : ${stat.base_stat} Effort: ${stat.effort}"
                    }
                    addView(text)
                }
                visibility = View.VISIBLE
            }

            pokemonTypes.apply {
                removeAllViews()
                pokemonDetails.types.forEach { type ->
                    val chip = Chip(requireContext()).apply {
                        text = type.type.name
                        isCheckable = false
                    }
                    addView(chip)
                }
                visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
