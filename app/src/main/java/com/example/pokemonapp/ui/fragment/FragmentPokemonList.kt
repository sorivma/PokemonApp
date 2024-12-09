package com.example.pokemonapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokemonapp.data.room.PokemonRepository
import com.example.pokemonapp.data.service.PokeApiService
import com.example.pokemonapp.data.service.RetrofitInstance
import com.example.pokemonapp.databinding.FragmentPokemonListBinding
import com.example.pokemonapp.ui.adapter.PokemonAdapter
import com.example.pokemonapp.ui.viewmodel.PokemonViewModel
import com.example.pokemonapp.ui.viewmodel.factory.PokemonViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FragmentPokemonList : Fragment() {
    private var _binding: FragmentPokemonListBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("ViewBinding not initialized")

    private lateinit var adapter: PokemonAdapter
    private lateinit var repository: PokemonRepository
    private lateinit var apiService: PokeApiService
    private val viewModel: PokemonViewModel by viewModels {
        PokemonViewModelFactory(repository, apiService)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokemonListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupSwipeRefresh()

        binding.buttonPrev.setOnClickListener {
            viewModel.prevPage()
        }

        binding.buttonNext.setOnClickListener {
            viewModel.nextPage()
        }

        observePokemons()
        observeBackBtnEnabled()
    }

    private fun setupViewModel() {
        repository = PokemonRepository.getInstance(requireContext())
        apiService = RetrofitInstance.apiService
    }

    private fun setupRecyclerView() {
        adapter = PokemonAdapter(
            mutableListOf(),
            onPokeClick = { pokemon ->
                val action = FragmentPokemonListDirections
                    .actionFragmentPokemonListToFragmentPokemonDetail(pokemon)
                findNavController().navigate(action)
            },
            onEditClick = { pokemon ->
                val action = FragmentPokemonListDirections
                    .actionFragmentPokemonListToPokeEditFragment(pokemon)
                findNavController().navigate(action)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            if (isRecyclerViewAtTop()) {
                viewModel.fetchPokemonsFromApi()
            }
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun observePokemons() {
        lifecycleScope.launch {
            viewModel.pokemons.collect { pokemons ->
                adapter.addPokemons(pokemons)
                Log.i("FragmentPokemonList", "The pokemon list updated")

                if (pokemons.isEmpty()) {
                    viewModel.fetchPokemonsFromApi()
                }
            }
        }
    }

    private fun observeBackBtnEnabled() {
        lifecycleScope.launch {
            viewModel.currentPage.collectLatest { currentPage ->
                if (currentPage == 0) {
                    binding.buttonPrev.isEnabled = false
                } else {
                    binding.buttonPrev.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isRecyclerViewAtTop(): Boolean {
        return (binding.recyclerView.layoutManager as? LinearLayoutManager)
            ?.findFirstCompletelyVisibleItemPosition() == 0
    }
}

