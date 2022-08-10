package com.simonediberardino.pokmoniquii.entities

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import com.simonediberardino.pokmoniquii.MainActivity
import com.simonediberardino.pokmoniquii.http.Utils

data class Pokemon(
    val activity: MainActivity,
    val id: Int,
    val idString: String,
    val name: String,
    var image: ImageView,
    val view: View,
    var isSaved: Boolean = false){

    init {
        fetch()
        updateImage()
    }

    internal fun update(){
        if(isSaved){
            activity.dbHandler.addNewPokemon(this)
        }else activity.dbHandler.deletePokemon(this)
    }

    private fun fetch(){
        isSaved = activity.dbHandler.isPokemonSaved(this)
    }

    private fun updateImage() {
        Thread{
            val imageBitmap = getImageBitmap()
            activity.runOnUiThread {
                image.setBackgroundResource(0)
                image.setImageBitmap(imageBitmap)
            }
        }.start()
    }

    private fun getImageBitmap(): Bitmap? {
        val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
        return Utils.bitmapFromUrl(url)
    }

}