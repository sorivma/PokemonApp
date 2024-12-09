package com.example.pokemonapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pokemonapp.data.model.PokemonDetails
import com.example.pokemonapp.data.service.PokeApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PokemonProfileViewModel: ViewModel() {
    private val _pokemonDetails = MutableLiveData<PokemonDetails?>()
    private val pokemonDetails: LiveData<PokemonDetails?> get() = _pokemonDetails

    fun getPokemonDetails(pokemonId: String): LiveData<PokemonDetails?> {
        PokeApiService.create().getPokemonDetails(pokemonId).enqueue(object :
            Callback<PokemonDetails> {
            override fun onResponse(call: Call<PokemonDetails>, response: Response<PokemonDetails>) {
                _pokemonDetails.value = response.body()
                Log.d("PokemonProfileViewModel","Pockemon details fetched: ${response.body()}")
            }

            override fun onFailure(call: Call<PokemonDetails>, t: Throwable) {
                _pokemonDetails.value = null
                Log.w("PokemonProfileViewModel", "Failed to fetch ${t.message}")
            }
        })
        return pokemonDetails
    }
}
