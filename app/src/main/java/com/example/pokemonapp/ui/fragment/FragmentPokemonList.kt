package com.example.pokemonapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokemonapp.R
import com.example.pokemonapp.data.model.PokemonResponse
import com.example.pokemonapp.data.service.PokeApiService
import com.example.pokemonapp.ui.adapter.PokemonAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FragmentPokemonList : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PokemonAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pokemon_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchPokemons()
    }

    private fun fetchPokemons() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(PokeApiService::class.java)
        service.getPokemons().enqueue(object : Callback<PokemonResponse> {
            override fun onResponse(call: Call<PokemonResponse>, response: retrofit2.Response<PokemonResponse>) {
                response.body()?.let {
                    adapter = PokemonAdapter(it.results.toMutableList()) { pokemon ->
                        val action = FragmentPokemonListDirections.actionFragmentPokemonListToFragmentPokemonDetail(pokemon)
                        findNavController().navigate(action)
                    }
                    recyclerView.adapter = adapter
                }
            }

            override fun onFailure(call: Call<PokemonResponse>, t: Throwable) {
                // TODO
            }
        })
    }
}