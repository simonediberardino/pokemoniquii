package com.simonediberardino.pokmoniquii.activities.main

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doOnTextChanged
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.simonediberardino.pokmoniquii.R
import com.simonediberardino.pokmoniquii.activities.AppCompatActivityV2
import com.simonediberardino.pokmoniquii.databinding.FragmentPokedexBinding
import com.simonediberardino.pokmoniquii.entities.PokemonInList
import com.simonediberardino.pokmoniquii.http.HttpPokemonListResponse
import com.simonediberardino.pokmoniquii.http.PokemonReferenceResponse
import com.simonediberardino.pokmoniquii.data.CacheData
import com.simonediberardino.pokmoniquii.ui.CDialog
import com.simonediberardino.pokmoniquii.utils.Error
import com.simonediberardino.pokmoniquii.utils.Utils
import com.simonediberardino.pokmoniquii.utils.Utils.isAtBottom

class PokedexFragment : PokemonListFragment() {
    override var _binding: Any? = null
    private val binding get() = (_binding as FragmentPokedexBinding)
    private var httpPokemonListResponse: HttpPokemonListResponse? = null
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

    override fun initializeFragment(inflater: LayoutInflater, container: ViewGroup?){
        CacheData.savedPokemons.clear()

        _binding = FragmentPokedexBinding.inflate(inflater, container, false)
        linearLayout = binding.pokedexPokemonLl

        scrollView = binding.pokedexScrollView.also {
            it.setOnScrollChangeListener { _, _, _, _, _ -> if(scrollView.isAtBottom()) nextPage() }
        }

        etSearchBar = binding.pokedexEtSearch

        this.populateLinearLayout()
    }

    private fun nextPage(){
        if(Utils.isInternetAvailable(activity ?: return))
            requestPokemonListFromApi()
        else Error(activity ?: return).show(Error.CODE.NO_INTERNET)
    }

    private fun populateLinearLayout(){
        if(!Utils.isInternetAvailable(activity as Activity)) {
            inflateCachedPokemon()
        }else{
            requestPokemonListFromApi()
        }
    }

    private fun requestPokemonListFromApi(){
        Thread{
            val endPoint = when {
                httpPokemonListResponse == null ->  "https://pokeapi.co/api/v2/pokemon?offset=${CacheData.savedPokemons.size}&limit=${CacheData.ITEMS_PER_PAGE}"
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


    private fun inflateCachedPokemon(){
        CacheData.getPokemonList().forEach {
            // Needed to prevent errors while casting LinkedMapTree to Pokemon.
            it as LinkedTreeMap<*,*>
            val pokemonViewGroup = layoutInflater.inflate(R.layout.pokemon_tab_template, null)
            inflatePokemon(
                PokemonInList(
                    (it["id"] as Double).toInt(),
                    it["name"] as String,
                    pokemonViewGroup.findViewById(R.id.pokemon_iv),
                    pokemonViewGroup as ViewGroup
                )
            )
        }
    }

    private fun showPokemons(httpPokemonListResponse: HttpPokemonListResponse){
        this.httpPokemonListResponse = httpPokemonListResponse
        this.httpPokemonListResponse!!.results.forEach { inflatePokemon(it) }
    }

    // Inflates the view that represents the pokemon http response
    private fun inflatePokemon(pokemonReferenceResponse: PokemonReferenceResponse){
        val pokemonViewGroup = layoutInflater.inflate(R.layout.pokemon_tab_template, null)

        val pokemon = PokemonInList(
            pokemonReferenceResponse.id,
            pokemonReferenceResponse.name,
            pokemonViewGroup.findViewById(R.id.pokemon_iv),
            pokemonViewGroup as ViewGroup
        )

        inflatePokemon(pokemon)
    }

    private fun inflatePokemon(pokemon: PokemonInList){
        updateSavedImage(pokemon)

        savedViews.add(pokemon.viewGroup)

        pokemon.viewGroup.setOnClickListener {
            pokemon.showStats(activity as AppCompatActivityV2? ?: return@setOnClickListener)
        }

        // Pokemon ID text view
        pokemon.viewGroup.findViewById<TextView>(R.id.pokemon_id_tv).also {
            it.text = pokemon.idString
        }

        // Pokemon name text view
        pokemon.viewGroup.findViewById<TextView>(R.id.pokemon_name_tv).also {
            it.text = pokemon.name
        }

        // Pokemon toggle image view
        pokemon.viewGroup.findViewById<ImageView>(R.id.pokemon_toggle_iv).also {
            // Updates the imageview and removes or adds the pokemon from/to the database
            it.setOnClickListener {
                if(pokemon.isSaved){
                    CDialog(
                        activity ?: return@setOnClickListener,
                        activity?.getString(R.string.confirm_pokemon_delete)?.replace("{pokemon}", pokemon.name) ?: return@setOnClickListener,
                        {
                            pokemon.isSaved = false
                            pokemon.update()
                            updateSavedImage(pokemon)
                        }
                    ).show()
                }else{
                    pokemon.isSaved = !pokemon.isSaved
                    pokemon.update()
                    updateSavedImage(pokemon)
                }
            }
        }

        // Finally adds the view to the linear layout
        activity?.runOnUiThread {
            (pokemon.viewGroup.parent as ViewGroup?)?.removeView(pokemon.viewGroup)
            linearLayout.addView(pokemon.viewGroup)
        }
    }

    private fun updateSavedImage(pokemon: PokemonInList){
        val toggleView = pokemon.viewGroup.findViewById<ImageView>(R.id.pokemon_toggle_iv)

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