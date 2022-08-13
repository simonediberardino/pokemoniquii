package com.simonediberardino.pokmoniquii.http

data class StatsReferenceResponse(
    val stats: Array<BaseStats>,
    val base_experience: Int,
    val weight: Int,
    val height: Int
    ){
    val hp get() = stats[0].base_stat

    data class BaseStats(val base_stat: Int)
}

