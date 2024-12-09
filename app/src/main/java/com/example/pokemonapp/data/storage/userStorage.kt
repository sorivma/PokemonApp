package com.example.pokemonapp.data.storage

import com.example.pokemonapp.data.model.User

private val users = mutableMapOf(
    "sorivma" to User(
        email = "sorivma",
        password = "sorivma",
        username = "sorivma"
    )
)

fun saveUser(user: User) { users[user.email] = user }

fun loginAs(email: String, password: String): Boolean = users[email]?.password == password
