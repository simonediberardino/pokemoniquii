package com.simonediberardino.pokmoniquii.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.simonediberardino.pokmoniquii.R
import com.simonediberardino.pokmoniquii.databinding.FragmentSavedBinding
import com.simonediberardino.pokmoniquii.entities.Pokemon
import com.simonediberardino.pokmoniquii.sharedprefs.CacheData


// TODO: get data from db, implement search bar, optimize code
class SavedPokemonFragment : Fragment() {
    private lateinit var gridLayout: GridLayout
    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        gridLayout = binding.savedGridLayout

        this.inflateCachedPokemons()
        return binding.root
    }

    private fun inflateCachedPokemons(){
        CacheData.savedPokemons.filter { it.isSaved }.forEach { inflatePokemon(it) }
    }

    private fun getPokemonListFromDb(){

    }

    private fun inflatePokemon(pokemon: Pokemon){
        val pokemonViewGroup = layoutInflater.inflate(R.layout.pokemon_box_template, null)

        pokemonViewGroup.findViewById<ImageView>(R.id.pokemon_iv).also {
            Thread{
                val bitmap = pokemon.bitmapFromUrl ?: return@Thread
                requireActivity().runOnUiThread { it.setImageBitmap(bitmap) }
            }.start()
        }

        pokemonViewGroup.findViewById<TextView>(R.id.pokemon_name_tv).also {
            it.text = pokemon.name
        }

        gridLayout.addView(pokemonViewGroup)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}