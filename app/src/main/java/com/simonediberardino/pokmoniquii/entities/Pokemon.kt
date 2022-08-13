package com.simonediberardino.pokmoniquii.entities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.simonediberardino.pokmoniquii.activities.AppCompatActivityV2
import com.simonediberardino.pokmoniquii.activities.main.StatsBottomSheet
import com.simonediberardino.pokmoniquii.activities.stats.StatsActivity
import com.simonediberardino.pokmoniquii.data.CacheData
import com.simonediberardino.pokmoniquii.http.StatsReferenceResponse
import com.simonediberardino.pokmoniquii.utils.Utils
import com.simonediberardino.pokmoniquii.utils.Utils.capitalizeWords

open class Pokemon(
    _id: Int = 0,
    _name: String = String(),
    _isSaved: Boolean = false,
    var weight: Int = -1,
    var height: Int = -1,
    var hp: Int = -1,
    var xp: Int = -1
) {
    open var id: Int = _id
    open var name: String = _name.capitalizeWords()
    var isSaved: Boolean = _isSaved
    val idString get() = "#${(id)}"

    fun showStats(activity: AppCompatActivityV2){
        fetchStats{
            val intent = Intent(activity, StatsActivity::class.java)
            intent.putExtra("pokemonId", id)
            activity.startActivity(intent)
        }
    }

    fun fetchStats(callback: Runnable){
        Thread{
            val endPoint = "https://pokeapi.co/api/v2/pokemon/${name.lowercase()}"

            endPoint.httpGet().responseJson { _, _, result ->
                when (result) {
                    is Result.Success -> {
                        val responseJson = result.get().obj().toString()
                        val response =
                            Gson().fromJson(responseJson, StatsReferenceResponse::class.java)

                        applyStats(response)
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

        return Utils.bitmapFromUrl(url, CacheData.context)
    }

    protected fun fetch() {
        isSaved = CacheData.context.dbHandler.isPokemonFavorite(this)
        CacheData.savePokemon(this)
    }
}