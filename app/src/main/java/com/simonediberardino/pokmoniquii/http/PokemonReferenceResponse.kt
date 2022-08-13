package com.simonediberardino.pokmoniquii.http

/**
 * Represents the response related to a specific Pokemon from the API.
 * Used by the Gson API to convert the response to a Kotlin Object
 */
data class PokemonReferenceResponse(val name: String, val url: String){
    val id
        get() = (url.reversed().drop(1).split("/")[0]).reversed().toInt()
}