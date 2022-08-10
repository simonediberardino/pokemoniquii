package com.simonediberardino.pokmoniquii.sharedprefs

import android.annotation.SuppressLint
import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.simonediberardino.pokmoniquii.activities.MainActivity
import com.simonediberardino.pokmoniquii.entities.Pokemon
import java.io.*


@SuppressLint("StaticFieldLeak")
object CacheData {
    lateinit var context: MainActivity
    var savedPokemons: MutableList<Pokemon> = mutableListOf()
    private const val FILE_NAME = "data.json"

    private fun read(context: Context): String? {
        return try {
            val fis: FileInputStream = context.openFileInput(FILE_NAME)
            val isr = InputStreamReader(fis)
            val bufferedReader = BufferedReader(isr)
            val sb = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            sb.toString()
        } catch (fileNotFound: FileNotFoundException) {
            null
        } catch (ioException: IOException) {
            null
        }
    }

    private fun update(jsonString: String?): Boolean {
        return try {
            val fos: FileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)!!
            if (jsonString != null) {
                fos.write(jsonString.toByteArray())
            }
            fos.close()
            true
        } catch (fileNotFound: FileNotFoundException) {
            false
        } catch (ioException: IOException) {
            false
        }
    }

    private fun isFilePresent(): Boolean {
        val path = """${context.filesDir.absolutePath}/$FILE_NAME"""
        val file = File(path)
        return file.exists()
    }

    private fun getApplicationData(): String {
        return if (isFilePresent()) {
            read(context)!!
        } else {
            update("{}")
            "{}"
        }
    }

    private fun savePokemonList(mutableList: MutableList<Pokemon>){
        // Builds a new mutable list based on the previous one. This is a workaround to prevent errors from the Jackson library.
        val dataTemplate = DataTemplate()
        mutableList.forEach {
            dataTemplate.data.add(
                Pokemon(it.id, it.idString, it.name)
            )
        }

        val ow = ObjectMapper().writer().withDefaultPrettyPrinter()
        val json = ow.writeValueAsString(dataTemplate)

        update(json)
    }

    fun getPokemonList(): MutableList<Pokemon> {
        val savedJson: String = getApplicationData()

        if(savedJson == "[{}]") return mutableListOf()
        return Gson().fromJson(savedJson, DataTemplate::class.java).data
    }


    fun savePokemon(pokemon: Pokemon) {
        val pokemonList = getPokemonList()

        if(pokemonList.any { it.id == pokemon.id})
            return

        pokemonList.add(pokemon)
        savePokemonList(pokemonList)
    }


    @SuppressLint("NewApi")
    fun deletePokemon(pokemon: Pokemon){
        val temp = getPokemonList()
        temp.add(pokemon)

        temp.removeIf { it.id == pokemon.id }
        savePokemonList(temp)
    }
}