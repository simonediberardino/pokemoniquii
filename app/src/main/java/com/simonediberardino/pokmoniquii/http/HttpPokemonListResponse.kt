package com.simonediberardino.pokmoniquii.http

data class HttpPokemonListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: Array<PokemonReferenceResponse>)