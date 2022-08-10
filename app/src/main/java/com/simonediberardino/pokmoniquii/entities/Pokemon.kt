package com.simonediberardino.pokmoniquii.entities

import android.graphics.Bitmap
import com.simonediberardino.pokmoniquii.http.Utils
import com.simonediberardino.pokmoniquii.sharedprefs.CacheData

open class Pokemon(_id: Int = 0, _idString: String = String(), _name: String = String(), _isSaved: Boolean = false) {
    open var id: Int = _id
    open var idString: String = _idString
    open var name: String = _name
    var isSaved: Boolean = _isSaved

    val bitmapFromUrl: Bitmap?
        get() {
            val url =
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"

            return Utils.bitmapFromUrl(url, CacheData.context)
        }

    protected fun fetch() {
        isSaved = CacheData.context.dbHandler.isPokemonSaved(this)
        CacheData.savePokemon(this)
    }
}