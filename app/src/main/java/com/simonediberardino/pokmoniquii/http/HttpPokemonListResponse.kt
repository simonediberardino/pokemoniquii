package com.simonediberardino.pokmoniquii.http

data class HttpPokemonListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: Array<PokemonReferenceResponse>)

data class PokemonReferenceResponse(val name: String, val url: String){
    val id
        get() = (url.reversed().drop(1).split("/")[0]).reversed().toInt()

    val idString get() = "#${(id)}"
}