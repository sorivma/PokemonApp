package com.example.pokemonapp.data.model

import java.io.Serializable

data class Pokemon(
    val name: String,
    val url: String,
): Serializable {

    val imageUrl: String
        get() {
            val id = url.split("/").dropLast(1).last()
            return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
        }

    val id: String
        get() = url.split("/").dropLast(1).last()
}

data class PokemonDetails(
    val baseExperience: Int,
    val height: Int,
    val weight: Int,
    val abilities: List<AbilityItem>,
    val moves: List<MoveItem>,
    val types: List<TypeItem>,
    val stats: List<StatItem>,
)

data class AbilityItem(val ability: Ability)
data class MoveItem(val move: Move)
data class TypeItem(val type: Type)
data class StatItem(val base_stat: Int, val effort: Int, val stat: Stat)
data class Ability(val name: String)
data class Move(val name: String)
data class Type(val name: String)
data class Stat(val name: String)

data class PokemonResponse(
    val results: List<Pokemon>
)