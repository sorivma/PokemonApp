package com.example.pokemonapp.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.pokemonapp.R
import com.example.pokemonapp.databinding.ActivityMainBinding
import com.example.pokemonapp.ui.fragment.SettingsFragment
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


// Single instance of DataStore for preferences
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
lateinit var dataStore: DataStore<Preferences>

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        com.example.pokemonapp.ui.dataStore = dataStore

        // Set up the ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the toolbar
        setSupportActionBar(binding.toolbar)

        lifecycleScope.launch {
            dataStore.data.map {
                it[SettingsFragment.PreferenceKeys.TOOLBAR_COLOR_PREFERENCE]
                    ?: getColorHex(R.color.teal_700)
            }.collect {
                binding.toolbar.setBackgroundColor(Color.parseColor(it))
            }
        }

        // Initialize the NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set the click listener on the settings button
        binding.settingsButton.setOnClickListener {
            // Check if the fragment is properly attached and the navController is valid
            if (navController.currentDestination != null) {
                navController.navigate(R.id.settingsFragment)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun getColorHex(resourceId: Int): String {
        val color = ContextCompat.getColor(this, resourceId)

        return String.format("#%06X", (0xFFFFFF and color))
    }
}
