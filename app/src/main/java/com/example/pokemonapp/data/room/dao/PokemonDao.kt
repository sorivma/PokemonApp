package com.example.pokemonapp.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pokemonapp.data.room.entity.PokemonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pokemons: List<PokemonEntity>)

    @Query("SELECT * FROM pokemon LIMIT :limit OFFSET :offset")
    suspend fun getPaginatedPokemons(limit: Int, offset: Int): List<PokemonEntity>

    @Query("SELECT * FROM pokemon LIMIT :limit OFFSET :offset")
    fun getPaginatedPokemonsFlow(limit: Int, offset: Int): Flow<List<PokemonEntity>>

    @Query("UPDATE pokemon SET name = :newName WHERE name = :currentName")
    suspend fun updatePokemonName(currentName: String, newName: String)

    @Query("DELETE FROM pokemon WHERE name = :pokeName")
    suspend fun deletePokemonByName(pokeName: String)
}