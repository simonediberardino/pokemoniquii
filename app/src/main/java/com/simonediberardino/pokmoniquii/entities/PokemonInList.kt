package com.simonediberardino.pokmoniquii.entities

import android.view.ViewGroup
import android.widget.ImageView
import com.simonediberardino.pokmoniquii.activities.AppCompatActivityV2
import com.simonediberardino.pokmoniquii.data.CacheData
import com.simonediberardino.pokmoniquii.activities.AppCompatActivityV2.*
import com.simonediberardino.pokmoniquii.activities.main.MainActivity
import com.simonediberardino.pokmoniquii.utils.Utils
import com.simonediberardino.pokmoniquii.utils.Utils.capitalizeWords

/**
 * Represents the Pokemon object attached to a specific view
 */
class PokemonInList(
    override var id: Int,
    override var name: String,
    var imageView: ImageView,
    var viewGroup: ViewGroup
) : Pokemon() {

    init {
        name = name.capitalizeWords()
        CacheData.savedPokemons.add(this)
        updateImage()
        fetch()
    }

    internal fun update() {
        if (isSaved) {
            (AppCompatActivityV2.lastInstance as MainActivity).dbHandler.addNewPokemon(this)
        } else {
            (AppCompatActivityV2.lastInstance as MainActivity).dbHandler.deletePokemon(this)
        }
    }

    private fun updateImage() {
        Thread{
            val bitmap = bitmapFromUrl()
            (AppCompatActivityV2.lastInstance as MainActivity).runOnUiThread { imageView.setImageBitmap(bitmap) }
        }.start()
    }

}