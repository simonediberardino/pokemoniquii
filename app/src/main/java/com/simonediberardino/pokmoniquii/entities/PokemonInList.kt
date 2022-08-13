package com.simonediberardino.pokmoniquii.entities

import android.view.ViewGroup
import android.widget.ImageView
import com.simonediberardino.pokmoniquii.data.CacheData
import com.simonediberardino.pokmoniquii.data.CacheData.context
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
            context.dbHandler.addNewPokemon(this)
        } else {
            context.dbHandler.deletePokemon(this)
        }
    }

    private fun updateImage() {
        Thread{
            val bitmap = bitmapFromUrl()
            context.runOnUiThread { imageView.setImageBitmap(bitmap) }
        }.start()
    }

}