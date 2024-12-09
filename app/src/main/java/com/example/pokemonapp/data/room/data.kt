package com.example.pokemonapp.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pokemonapp.data.model.Pokemon
import com.example.pokemonapp.data.model.PokemonResponse
import com.example.pokemonapp.data.room.dao.PokemonDao
import com.example.pokemonapp.data.room.entity.PokemonEntity
import com.example.pokemonapp.data.service.PokeApiService
import com.example.pokemonapp.data.service.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Database(entities = [PokemonEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
}

class PokemonRepository(private val dao: PokemonDao) {
    suspend fun savePokemons(pokemons: List<Pokemon>) {
        dao.insertAll(pokemons.map { PokemonEntity(it.name, it.url) })
    }

    suspend fun getPokemons(limit: Int, offset: Int): List<PokemonEntity> {
        return dao.getPaginatedPokemons(limit, offset)
    }

    fun getPaginatedPokemonFlow(limit: Int, offset: Int): Flow<List<Pokemon>> {
        return dao.getPaginatedPokemonsFlow(limit, offset).map { entryList ->
            entryList.map {
                Pokemon(
                    name = it.name,
                    url = it.url
                )
            }
        }
    }

    suspend fun updatePokeName(oldName: String, newName: String) {
        dao.updatePokemonName(
            currentName = oldName,
            newName = newName
        )
    }

    suspend fun deletePoke(name: String) {
        dao.deletePokemonByName(name)
    }

    fun fetchPokemonsFromApi(offset: Int, limit: Int): Call<PokemonResponse> {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(PokeApiService::class.java)
        return service.getPokemons(offset, limit)
    }

    companion object {
        @Volatile
        private var INSTANCE: PokemonRepository? = null

        fun getInstance(context: Context): PokemonRepository {
            return INSTANCE ?: synchronized(this) {
                val database = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pokemon_database"
                ).build()

                val dao = database.pokemonDao()

                val instance = PokemonRepository(dao)
                INSTANCE = instance
                instance
            }
        }
    }
}