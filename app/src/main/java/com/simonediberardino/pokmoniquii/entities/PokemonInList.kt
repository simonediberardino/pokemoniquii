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
    var viewGroup: ViewGroup,
    override var isSaved: Boolean = false,
    override var weight: Int = -1,
    override var height: Int = -1,
    override var hp: Int = -1,
    override var xp: Int = -1
) : Pokemon() {

    init {
        name = name.capitalizeWords()

        if(!CacheData.savedPokemons.any { it.id == this.id })
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
            val bitmap = bitmapFromUrl() ?: return@Thread
            (AppCompatActivityV2.lastInstance).runOnUiThread { imageView.setImageBitmap(bitmap) }
        }.start()
    }

}