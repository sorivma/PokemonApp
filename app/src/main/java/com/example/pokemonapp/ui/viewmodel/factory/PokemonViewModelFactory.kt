package com.example.pokemonapp.ui.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pokemonapp.data.room.PokemonRepository
import com.example.pokemonapp.data.service.PokeApiService
import com.example.pokemonapp.ui.viewmodel.PokemonViewModel

class PokemonViewModelFactory(
    private val repository: PokemonRepository,
    private val apiService: PokeApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PokemonViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PokemonViewModel(repository, apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}