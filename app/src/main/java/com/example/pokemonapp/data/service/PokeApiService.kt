package com.example.pokemonapp.data.service

import com.example.pokemonapp.data.model.PokemonDetails
import com.example.pokemonapp.data.model.PokemonResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApiService {
    @GET("api/v2/pokemon?limit=100")
    fun getPokemons(): Call<PokemonResponse>

    @GET("api/v2/pokemon/{id}")
    fun getPokemonDetails(@Path("id") id: String): Call<PokemonDetails>

    companion object {
        fun create(): PokeApiService = Retrofit.Builder()
                .baseUrl("https://pokeapi.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PokeApiService::class.java)
    }
}