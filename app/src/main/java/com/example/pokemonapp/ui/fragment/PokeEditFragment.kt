package com.example.pokemonapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.pokemonapp.data.room.PokemonRepository
import com.example.pokemonapp.databinding.FragmentPokeEditBinding
import kotlinx.coroutines.launch

class PokeEditFragment : Fragment() {
    private val args: PokeEditFragmentArgs by navArgs()
    private var _binding: FragmentPokeEditBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("ViewBinding not initialized")

    private lateinit var repository: PokemonRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokeEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = PokemonRepository.getInstance(requireContext())

        binding.pokeNameInput.setText(args.poke.name)

        binding.updatePokeBtn.setOnClickListener {
            lifecycleScope.launch {
                repository.updatePokeName(args.poke.name, binding.pokeNameInput.text.toString())
                navigateToList()
            }
        }

        binding.deletePokeBtn.setOnClickListener {
            lifecycleScope.launch {
                repository.deletePoke(args.poke.name)
                navigateToList()
            }
        }
    }

    private fun navigateToList() {
        val action = PokeEditFragmentDirections.actionPokeEditFragmentToFragmentPokemonList()
        findNavController().navigate(action)
    }
}