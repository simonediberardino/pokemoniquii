package com.simonediberardino.pokmoniquii.entities

import android.content.Intent
import android.graphics.Bitmap
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.simonediberardino.pokmoniquii.activities.AppCompatActivityV2
import com.simonediberardino.pokmoniquii.activities.main.MainActivity
import com.simonediberardino.pokmoniquii.activities.stats.StatsActivity
import com.simonediberardino.pokmoniquii.data.CacheData
import com.simonediberardino.pokmoniquii.http.StatsReferenceResponse
import com.simonediberardino.pokmoniquii.utils.Utils
import com.simonediberardino.pokmoniquii.utils.Utils.capitalizeWords

open class Pokemon(
    open var id: Int = 0,
    _name: String = String(),
    open var isSaved: Boolean = false,
    open var weight: Int = -1,
    open var height: Int = -1,
    open var hp: Int = -1,
    open var xp: Int = -1
) {
    open var name: String = _name.capitalizeWords()
    val idString get() = "#${(id)}"

    init {
        fetchStats()
    }

    fun showStats(activity: AppCompatActivityV2){
        val intent = Intent(activity, StatsActivity::class.java)
        intent.putExtra("pokemonId", id)
        activity.startActivity(intent)
    }

    private fun fetchStats(callback: Runnable = Runnable {}){
        if(!Utils.isInternetAvailable())
            return

        Thread{
            val endPoint = "https://pokeapi.co/api/v2/pokemon/${name.lowercase()}"

            endPoint.httpGet().responseJson { _, _, result ->
                when (result) {
                    is Result.Success -> {
                        val responseJson = result.get().obj().toString()
                        val response =
                            Gson().fromJson(responseJson, StatsReferenceResponse::class.java)

                        applyStats(response)
                        save()
                        callback.run()
                    }
                    else -> return@responseJson
                }
            }
        }.start()
    }

    private fun applyStats(response: StatsReferenceResponse) {
        height = response.height
        weight = response.weight
        hp = response.hp
        xp = response.base_experience
    }

    fun bitmapFromUrl(): Bitmap? {
        val url =
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"

        return Utils.bitmapFromUrl(url, AppCompatActivityV2.lastInstance)
    }

    protected fun fetch() {
        isSaved = (AppCompatActivityV2.lastInstance as MainActivity).dbHandler.isPokemonFavorite(this)
    }

    protected fun save(){
        AppCompatActivityV2.lastInstance.runOnUiThread {
            if(Utils.isInternetAvailable())
                CacheData.savePokemon(this)
        }
    }
}