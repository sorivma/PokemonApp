package com.example.pokemonapp.ui.fragment

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.pokemonapp.R
import com.example.pokemonapp.databinding.FragmentSettingsBinding
import com.example.pokemonapp.ui.dataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    private lateinit var darkColor: String
    private lateinit var lightColor: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        darkColor = getColorHex(R.color.teal_700)
        lightColor = getColorHex(R.color.teal_200)

        setupNightThemeSwitch()
        setupToolbarColorSwitch()
        displayPokeInfo()

        binding.deleteFileButton.setOnClickListener {
            saveFileToInternalStorage()
            deletePokemonsFile()
        }

        binding.restoreFileButton.setOnClickListener {
            restoreFileFromInternalStorage()
        }
    }

    private fun saveFileToInternalStorage() {
        val externalFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            "pokemons_dubrovskiAA.txt"
        )
        if (externalFile.exists()) {
            try {
                val internalFile = File(requireContext().filesDir, "pokemons_dubrovskiAA.txt")
                externalFile.inputStream().use { inputStream ->
                    FileOutputStream(internalFile).use { outputStream ->
                        val buffer = ByteArray(1024)
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                        }
                    }
                }
                Toast.makeText(
                    requireContext(),
                    "Pokémon file saved to internal storage.",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: IOException) {
                Toast.makeText(
                    requireContext(),
                    "Error saving file: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun restoreFileFromInternalStorage() {
        val internalFile = File(requireContext().filesDir, "pokemons_dubrovskiAA.txt")

        if (internalFile.exists()) {
            try {
                val externalFile = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    "pokemons_dubrovskiAA.txt"
                )
                internalFile.inputStream().use { inputStream ->
                    FileOutputStream(externalFile).use { outputStream ->
                        val buffer = ByteArray(1024)
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                        }
                    }
                }
                Toast.makeText(
                    requireContext(),
                    "Pokémon file restored to external storage.",
                    Toast.LENGTH_SHORT
                ).show()
                displayPokeInfo()  // Обновляем информацию о файле
            } catch (e: IOException) {
                Toast.makeText(
                    requireContext(),
                    "Error restoring file: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(requireContext(), "No backup file found.", Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun deletePokemonsFile() {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            "pokemons_dubrovskiAA.txt"
        )

        if (file.exists()) {
            val isDeleted = file.delete()
            if (isDeleted) {
                Toast.makeText(
                    requireContext(),
                    "Pokémon file deleted successfully.",
                    Toast.LENGTH_SHORT
                ).show()
                binding.pokeFileText.text = "No Pokémon file found."
            } else {
                Toast.makeText(
                    requireContext(),
                    "Failed to delete Pokémon file.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(requireContext(), "No Pokémon file found to delete.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun displayPokeInfo() {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            "pokemons_dubrovskiAA.txt"
        )

        binding.pokeFileText.text = if (file.exists()) {
            """
                PokeFile Info:
                File Size: ${getFileSize(file)} b
                Pokemon Count: ${getPokemonCount(file)}
            """.trimIndent()
        } else {
            "File does not exist"
        }
    }

    private fun getFileSize(file: File): Long {
        return if (file.exists()) {
            file.length()
        } else {
            0L
        }
    }

    private fun getPokemonCount(file: File): Int {
        var count = 0
        try {
            file.bufferedReader().useLines { lines ->
                count = lines.count()
            }
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Error reading file: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
        return count
    }

    private fun setupToolbarColorSwitch() {
        val selectedColorFlow = dataStore.data
            .map { preferences ->
                val color = preferences[PreferenceKeys.TOOLBAR_COLOR_PREFERENCE] ?: darkColor
                binding.colorSwitch.isChecked = color == lightColor
                color
            }

        lifecycleScope.launch {
            selectedColorFlow.collect { colorHex ->
                updateToolbarColor(colorHex)
            }
        }
        binding.colorSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                lifecycleScope.launch {
                    saveToolbarColor(lightColor)
                }
            } else {
                lifecycleScope.launch {
                    saveToolbarColor(darkColor)
                }
            }
        }
    }

    private suspend fun saveToolbarColor(colorHex: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.TOOLBAR_COLOR_PREFERENCE] = colorHex
        }
    }

    private fun updateToolbarColor(colorHex: String) {
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.setBackgroundColor(Color.parseColor(colorHex))
    }

    private fun setupNightThemeSwitch() {
        val pref =
            requireContext().getSharedPreferences(SharedPreferencesKeys.NIGHT_MODE, MODE_PRIVATE)

        val isNightMode = pref.getBoolean(SharedPreferencesKeys.NIGHT_MODE, false)

        binding.themeSwitch.isChecked = isNightMode

        binding.themeSwitch.setOnCheckedChangeListener { _, b ->
            val editor = requireContext().getSharedPreferences(SharedPreferencesKeys.NIGHT_MODE, MODE_PRIVATE).edit()
            editor.putBoolean(SharedPreferencesKeys.NIGHT_MODE, b)
            editor.apply()
        }
    }

    object PreferenceKeys {
        val TOOLBAR_COLOR_PREFERENCE = stringPreferencesKey("toolbar_color")
    }

    private fun getColorHex(resourceId: Int): String {
        val color = ContextCompat.getColor(requireContext(), resourceId)

        return String.format("#%06X", (0xFFFFFF and color))
    }

    object SharedPreferencesKeys {
        const val NIGHT_MODE = "night_mode"
    }
}
