package com.simonediberardino.pokmoniquii.activities.main

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.simonediberardino.pokmoniquii.R
import com.simonediberardino.pokmoniquii.activities.AppCompatActivityV2
import com.simonediberardino.pokmoniquii.databinding.ActivityMainBinding
import com.simonediberardino.pokmoniquii.data.CacheData
import com.simonediberardino.pokmoniquii.sqlite.DbHandler
import com.simonediberardino.pokmoniquii.ui.InfoDialog

class MainActivity : AppCompatActivityV2() {
    private lateinit var binding: ActivityMainBinding
    internal var dbHandler = DbHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_pokedex, R.id.navigation_pokemon)
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if(CacheData.isFirstStart()){
            InfoDialog(this).show()
            CacheData.setFirstStart(true)
        }
    }
}