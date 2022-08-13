package com.simonediberardino.pokmoniquii.activities.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.simonediberardino.pokmoniquii.R
import com.simonediberardino.pokmoniquii.activities.AppCompatActivityV2
import com.simonediberardino.pokmoniquii.databinding.FragmentSavedBinding
import com.simonediberardino.pokmoniquii.entities.Pokemon
import com.simonediberardino.pokmoniquii.http.PokemonReferenceResponse
import com.simonediberardino.pokmoniquii.utils.Utils
import com.simonediberardino.pokmoniquii.data.CacheData
import java.lang.IllegalStateException


class SavedPokemonFragment : PokemonListFragment() {
    override var _binding: Any? = null
    private val binding: FragmentSavedBinding get() = (_binding as FragmentSavedBinding)
    private lateinit var gridLayout: GridLayout
    override var etSearchBar: EditText? = null
        set(value) {
            field = value
            field?.doOnTextChanged { text, _, _, _ -> onTextUpdated(text.toString()) }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.initializeFragment(inflater, container)
        return binding.root
    }

    override fun initializeFragment(inflater: LayoutInflater, container: ViewGroup?) {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        gridLayout = binding.savedGridLayout
        etSearchBar = binding.savedEtSearch

        if(!Utils.isInternetAvailable(activity as MainActivity))
            this.inflateCachedPokemons()
        else fetchPokemonAndInflate()
    }

    /*
        Shows the favorite cached pokemon if internet is not available
     */
    private fun inflateCachedPokemons(){
        if(CacheData.savedPokemons.isNotEmpty())
            gridLayout.removeAllViews()

        CacheData.savedPokemons.sortBy { it.id }
        CacheData.savedPokemons.filter { it.isSaved }.forEach { inflatePokemon(it) }
    }

    private fun fetchPokemonAndInflate(){
        val savedPokemons = (activity as MainActivity).dbHandler.getSavedPokemonIds()

        if(savedPokemons.isNotEmpty())
            gridLayout.removeAllViews()

        savedPokemons.forEach { fetchPokemonAndInflate(it) }
    }

    private fun fetchPokemonAndInflate(pokemonId: String){
        Thread{
            val endPoint = "https://pokeapi.co/api/v2/pokemon/$pokemonId"

            endPoint.httpGet().responseJson { _, _, result ->
                when (result) {
                    is Result.Success -> {
                        val responseJson = result.get().obj().toString()
                        val pokemonReferenceResponse = Gson().fromJson(responseJson, PokemonReferenceResponse::class.java)

                        val pokemon = Pokemon(
                            pokemonId.toInt(),
                            pokemonReferenceResponse.name
                        )

                        inflatePokemon(pokemon)
                    }
                    else -> return@responseJson
                }
            }
        }.start()
    }

    @Throws(IllegalStateException::class)
    private fun inflatePokemon(pokemon: Pokemon){
        val pokemonViewGroup = layoutInflater.inflate(R.layout.pokemon_box_template, null)

        pokemonViewGroup.setOnClickListener {
            pokemon.showStats(activity as AppCompatActivityV2? ?: return@setOnClickListener)
        }

        pokemonViewGroup.findViewById<ImageView>(R.id.pokemon_iv).also {
            Thread{
                val bitmap = pokemon.bitmapFromUrl() ?: return@Thread
                activity?.runOnUiThread { it.setImageBitmap(bitmap) }
            }.start()
        }

        pokemonViewGroup.findViewById<TextView>(R.id.pokemon_name_tv).also {
            it.text = pokemon.name
        }

        savedViews.add(pokemonViewGroup)

        if(!CacheData.savedPokemons.any { it.id == pokemon.id }){
            CacheData.savedPokemons.add(pokemon)
        }

        activity?.runOnUiThread { gridLayout.addView(pokemonViewGroup) }
    }

}