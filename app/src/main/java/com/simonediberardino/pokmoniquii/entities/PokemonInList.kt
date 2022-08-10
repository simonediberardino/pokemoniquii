package com.simonediberardino.pokmoniquii.entities

import android.view.ViewGroup
import android.widget.ImageView
import com.simonediberardino.pokmoniquii.sharedprefs.CacheData
import com.simonediberardino.pokmoniquii.sharedprefs.CacheData.context

class PokemonInList(
    override var id: Int,
    override var idString: String,
    override var name: String,
    var imageView: ImageView,
    var viewGroup: ViewGroup
) : Pokemon() {

    init {
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
            val bitmap = bitmapFromUrl
            context.runOnUiThread { imageView.setImageBitmap(bitmap) }
        }.start()
    }

}