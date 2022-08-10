package com.simonediberardino.pokmoniquii.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.simonediberardino.pokmoniquii.R
import com.simonediberardino.pokmoniquii.databinding.FragmentHomeBinding
import com.simonediberardino.pokmoniquii.entities.PokemonInList
import com.simonediberardino.pokmoniquii.http.HttpPokemonListResponse
import com.simonediberardino.pokmoniquii.http.PokemonReferenceResponse
import com.simonediberardino.pokmoniquii.http.Utils
import com.simonediberardino.pokmoniquii.sharedprefs.CacheData

class HomeFragment : Fragment() {
    companion object{
        private val POKE_PER_PAGE = 10
    }

    private lateinit var etSearchBar: EditText
    private lateinit var linearLayout: LinearLayout
    private lateinit var showMoreBtn: Button
    private var savedViews: MutableList<View> = mutableListOf()
    private var httpPokemonListResponse: HttpPokemonListResponse? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        CacheData.savedPokemons.clear()
        linearLayout = binding.homePokemonLl
        showMoreBtn = binding.homeShowMore.also { it.setOnClickListener { requestPokemonListFromApi() } }
        etSearchBar = binding.homeEtSearch.also { it.doOnTextChanged { text, _, _, _ -> onTextUpdated(text.toString()) } }

        this.populateLinearLayout()
        return binding.root
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
                httpPokemonListResponse == null ->  "https://pokeapi.co/api/v2/pokemon?offset=${CacheData.savedPokemons.size}&limit=$POKE_PER_PAGE"
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
            val pokemonViewGroup = layoutInflater.inflate(R.layout.pokemon_tab_template, null)
            inflatePokemon(
                PokemonInList(
                    it.id,
                    it.idString,
                    it.name,
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
            pokemonReferenceResponse.idString,
            pokemonReferenceResponse.name,
            pokemonViewGroup.findViewById(R.id.pokemon_iv),
            pokemonViewGroup as ViewGroup
        )

        inflatePokemon(pokemon)
    }

    private fun inflatePokemon(pokemon: PokemonInList){
        updateSavedImage(pokemon)

        savedViews.add(pokemon.viewGroup)

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
                pokemon.isSaved = !pokemon.isSaved
                pokemon.update()
                updateSavedImage(pokemon)
            }
        }

        // Finally adds the view to the linear layout
        requireActivity().runOnUiThread {
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

    private fun onTextUpdated(text: String){
        for(view: View in savedViews){
            if(text.length <= 1) {
                view.visibility = View.VISIBLE
                continue
            }

            val pokemonName = view.findViewById<TextView>(R.id.pokemon_name_tv).text.toString()
            val pokemonId = view.findViewById<TextView>(R.id.pokemon_id_tv).text.toString()

            if(!pokemonName.contains(text) && !pokemonId.contains(text))
                view.visibility = View.GONE
            else view.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}