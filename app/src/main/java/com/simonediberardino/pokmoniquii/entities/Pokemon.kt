package com.simonediberardino.pokmoniquii.entities

import android.media.Image
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.simonediberardino.pokmoniquii.MainActivity
import com.simonediberardino.pokmoniquii.R

data class Pokemon(
    val activity: MainActivity,
    val id: Int,
    val idString: String,
    val name: String,
    var image: ImageView,
    val view: View){

    var isSaved = false
        set(value){
            field = value
            view.findViewById<ImageView>(R.id.pokemon_toggle_iv).setBackgroundResource(
                if(field)
                    R.drawable.pokeball_colorized
                else R.drawable.pokeball_bnw
            )
        }

    internal fun update(){
        if(isSaved){
            activity.dbHandler.addNewPokemon(this)
        }else activity.dbHandler.deletePokemon(this)
    }

    fun fetch(){
        isSaved = activity.dbHandler.isPokemonSaved(this)
        println("is $this saved $isSaved")
    }
}