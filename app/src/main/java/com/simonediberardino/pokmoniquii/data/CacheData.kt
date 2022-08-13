package com.simonediberardino.pokmoniquii.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.simonediberardino.pokmoniquii.activities.AppCompatActivityV2.Companion.lastInstance
import com.simonediberardino.pokmoniquii.activities.main.MainActivity
import com.simonediberardino.pokmoniquii.entities.Pokemon
import com.simonediberardino.pokmoniquii.entities.PokemonInList


/**
 *
 */
@SuppressLint("StaticFieldLeak")
object CacheData {
    var savedPokemons: MutableList<Pokemon> = mutableListOf()
    private const val SHARED_PREF_REF = "shared_prefs"
    private const val POKEMON_REF = "pokemon"
    private const val FIRST_START_REF = "firststart"

    private val applicationData: SharedPreferences
        get() {
            return lastInstance.getSharedPreferences(
                SHARED_PREF_REF,
                Context.MODE_PRIVATE
            )!!
        }

    fun isFirstStart(): Boolean {
        return applicationData.getBoolean(FIRST_START_REF, true)
    }

    fun setFirstStart(flag: Boolean){
        val dataEditor = applicationData.edit()
        dataEditor.putBoolean(FIRST_START_REF, flag)
        dataEditor.apply()
    }

    private fun readPokemonList(): String {
        return applicationData.getString(POKEMON_REF, "[]")!!
    }

    private fun updatePokemonList(jsonString: String){
        val dataEditor = applicationData.edit()
        dataEditor.putString(SHARED_PREF_REF, jsonString)
        dataEditor.apply()
    }

    private fun savePokemonList(mutableList: MutableList<Pokemon>){
        // Builds a new mutable list based on the previous one. This is a workaround to prevent errors from the Jackson library.
        val dataTemplate = mutableListOf<Any>()

        for(j: Any in mutableList){
            if(j is PokemonInList){
                dataTemplate.add(Pokemon(j.id, j.name))
            }else if(j is LinkedTreeMap<*,*>){
                dataTemplate.add(Pokemon((j["id"] as Double).toInt(), j["name"] as String))

            }
        }

        val ow = ObjectMapper().writer().withDefaultPrettyPrinter()
        val json = ow.writeValueAsString(dataTemplate)

        updatePokemonList(json)
    }

    fun getPokemonList(): MutableList<*> {
        val savedJson: String = readPokemonList()
        return Gson().fromJson(savedJson, MutableList::class.java)
    }

    fun savePokemon(pokemon: Pokemon) {
        val pokemonList = getPokemonList() as MutableList<LinkedTreeMap<*,*>>

        if(pokemonList.size < 50)
            return

        for(l in pokemonList)
            if((l["id"] as Double).toInt() == pokemon.id)
                return

        doSavePokemon(pokemon)
    }

    private fun doSavePokemon(pokemon: Pokemon){
        val pokemonList = (getPokemonList() as MutableList<Pokemon>)
        pokemonList.add(pokemon)
        savePokemonList(pokemonList)
    }
}