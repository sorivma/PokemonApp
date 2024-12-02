package com.example.pokemonapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokemonapp.data.model.PokemonResponse
import com.example.pokemonapp.data.service.PokeApiService
import com.example.pokemonapp.databinding.FragmentPokemonListBinding
import com.example.pokemonapp.ui.adapter.PokemonAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FragmentPokemonList : Fragment() {

    private var _binding: FragmentPokemonListBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("ViewBinding not initialized")

    private lateinit var adapter: PokemonAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokemonListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        fetchPokemons()
    }

    private fun fetchPokemons() {
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE

        val retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(PokeApiService::class.java)
        service.getPokemons().enqueue(object : Callback<PokemonResponse> {
            override fun onResponse(call: Call<PokemonResponse>, response: retrofit2.Response<PokemonResponse>) {
                binding.progressBar.visibility = View.GONE
                response.body()?.let { pokemonResponse ->
                    adapter = PokemonAdapter(pokemonResponse.results.toMutableList()) { pokemon ->
                        val action = FragmentPokemonListDirections.actionFragmentPokemonListToFragmentPokemonDetail(pokemon)
                        findNavController().navigate(action)
                    }
                    binding.recyclerView.adapter = adapter
                    binding.recyclerView.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<PokemonResponse>, t: Throwable) {
                // TODO: Handle the error appropriately
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
