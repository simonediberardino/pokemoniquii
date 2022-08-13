package com.simonediberardino.pokmoniquii.activities.stats

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.simonediberardino.pokmoniquii.R
import com.simonediberardino.pokmoniquii.activities.AppCompatActivityV2
import com.simonediberardino.pokmoniquii.data.CacheData
import com.simonediberardino.pokmoniquii.entities.Pokemon
import com.simonediberardino.pokmoniquii.utils.Utils
import java.util.*

class StatsActivity : AppCompatActivityV2() {
    private lateinit var nameTV: TextView
    private lateinit var pokemon: Pokemon
    private lateinit var pokemonIV: ImageView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var basexpPB: ProgressBar
    private lateinit var heightPB: ProgressBar
    private lateinit var weightPB: ProgressBar
    private lateinit var hpPB: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        getPokemon()
        initializeBottomSheet()
    }

    private fun getPokemon(){
        val pokemonId = intent.extras?.getInt("pokemonId")!!
        pokemon = CacheData.savedPokemons.first { it.id == pokemonId }
    }

    private fun initializeBottomSheet(){
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet_persistent))
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.isDraggable = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        pokemonIV = findViewById(R.id.stats_image)
        nameTV = findViewById(R.id.stats_name)
        basexpPB = findViewById(R.id.stats_xp_pb)
        weightPB = findViewById(R.id.stats_weight_pb)
        heightPB = findViewById(R.id.stats_height_pb)
        hpPB = findViewById(R.id.stats_hp_pb)

        Timer().schedule(object: TimerTask() {
            override fun run() {
                runOnUiThread { doShow() }
            }
        }, 250)
    }

    fun doShow(){
        nameTV.text = pokemon.name
        basexpPB.progress = pokemon.xp/10
        weightPB.progress = pokemon.weight/10
        heightPB.progress = pokemon.height
        hpPB.progress = pokemon.hp

        Thread{
            val bitmap = pokemon.bitmapFromUrl()
            runOnUiThread { pokemonIV.setImageBitmap(bitmap) }
        }.start()

        show()
    }

    private fun show() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun hide(){
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }


    override fun onBackPressed() {
        hide()
        super.onBackPressed()
    }
}