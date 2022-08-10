package com.simonediberardino.pokmoniquii.ui.home

import android.graphics.Bitmap
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.simonediberardino.pokmoniquii.MainActivity
import com.simonediberardino.pokmoniquii.R
import com.simonediberardino.pokmoniquii.databinding.FragmentHomeBinding
import com.simonediberardino.pokmoniquii.entities.Pokemon
import com.simonediberardino.pokmoniquii.http.HttpPokemonListResponse
import com.simonediberardino.pokmoniquii.http.PokemonReferenceResponse
import com.simonediberardino.pokmoniquii.http.Utils

class HomeFragment : Fragment() {
    private lateinit var linearLayout: LinearLayout
    private lateinit var showMoreBtn: Button
    private var pokemons: MutableList<Pokemon> = mutableListOf()
    private var httpPokemonListResponse: HttpPokemonListResponse? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        linearLayout = binding.homePokemonLl
        showMoreBtn = binding.homeShowMore.also { it.setOnClickListener { requestPokemonList() } }

        requestPokemonList()

        return binding.root
    }

    private fun requestPokemonList(){
        Thread{
            val endPoint = when {
                httpPokemonListResponse == null ->  "https://pokeapi.co/api/v2/pokemon?offset=0&limit=10"
                httpPokemonListResponse?.next == null -> return@Thread
                else -> httpPokemonListResponse!!.next
            }!!

            endPoint.httpGet().responseJson { _, _, result ->
                when (result) {
                    is Result.Success -> {
                        val responseJson = result.get().obj().toString()
                        val httpPokemonListResponse =
                            Gson().fromJson(responseJson, HttpPokemonListResponse::class.java)

                        showPokemons(httpPokemonListResponse)
                    }
                    else -> return@responseJson
                }
            }
        }.start()
    }

    private fun showPokemons(httpPokemonListResponse: HttpPokemonListResponse){
        this.httpPokemonListResponse = httpPokemonListResponse
        this.httpPokemonListResponse!!.results.forEach { inflatePokemon(it) }
    }

    // Inflates the view that represents the pokemon http response
    private fun inflatePokemon(pokemonReferenceResponse: PokemonReferenceResponse){
        val pokemonViewGroup = layoutInflater.inflate(R.layout.pokemon_tab_template, null)

        val pokemon = Pokemon(
            activity as MainActivity,
            pokemonReferenceResponse.id,
            pokemonReferenceResponse.idString,
            pokemonReferenceResponse.name,
            pokemonViewGroup.findViewById(R.id.pokemon_iv),
            pokemonViewGroup
        )

        pokemons.add(pokemon)
        updateSavedImage(pokemon)

        // Pokemon ID text view
        pokemonViewGroup.findViewById<TextView>(R.id.pokemon_id_tv).also {
            it.text = pokemon.idString
        }

        // Pokemon name text view
        pokemonViewGroup.findViewById<TextView>(R.id.pokemon_name_tv).also {
            it.text = pokemon.name
        }

        // Pokemon toggle image view
        pokemonViewGroup.findViewById<ImageView>(R.id.pokemon_toggle_iv).also {
            // Updates the imageview and removes or adds the pokemon from/to the database
            it.setOnClickListener {
                pokemon.isSaved = !pokemon.isSaved
                pokemon.update()
                updateSavedImage(pokemon)
            }
        }

        // Finally adds the view to the linear layout
        requireActivity().runOnUiThread {
            linearLayout.addView(pokemonViewGroup)
        }
    }

    private fun updateSavedImage(pokemon: Pokemon){
        val toggleView = pokemon.view.findViewById<ImageView>(R.id.pokemon_toggle_iv)

        toggleView.findViewById<ImageView>(R.id.pokemon_toggle_iv).setBackgroundResource(
            if(pokemon.isSaved)
                R.drawable.pokeball_colorized
            else R.drawable.pokeball_bnw
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}