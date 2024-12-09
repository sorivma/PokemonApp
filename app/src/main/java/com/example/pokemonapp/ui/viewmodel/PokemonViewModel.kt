package com.example.pokemonapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemonapp.data.model.Pokemon
import com.example.pokemonapp.data.room.PokemonRepository
import com.example.pokemonapp.data.service.PokeApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import retrofit2.await

class PokemonViewModel(
    private val repository: PokemonRepository,
    private val apiService: PokeApiService
) : ViewModel() {
    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage

    val pokemons: Flow<List<Pokemon>> = _currentPage
        .flatMapLatest { page ->
            repository.getPaginatedPokemonFlow(
                limit = PAGE_SIZE,
                offset = PAGE_SIZE * page
            )
        }

    fun fetchPokemonsFromApi() {
        Log.i("PockemonViewModel", "Api fetch triggered")
        viewModelScope.launch {
            try {
                val response = apiService.getPokemons(
                    offset = PAGE_SIZE * 0,
                    limit = PAGE_SIZE
                ).await()
                if (response.results.isNotEmpty()) {
                    repository.savePokemons(response.results)
                }
            } catch (e: Exception) {
                Log.e("PokemonViewModel", "Error fetching pokemons ${e.message}")
            }
        }
    }

    fun nextPage() {
        _currentPage.value ++
    }

    fun prevPage() {
        if (_currentPage.value > 0) {
            _currentPage.value--
        }
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}