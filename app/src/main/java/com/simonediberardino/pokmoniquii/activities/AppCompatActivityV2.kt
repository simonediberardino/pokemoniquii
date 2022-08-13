package com.simonediberardino.pokmoniquii.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.simonediberardino.pokmoniquii.R
import com.simonediberardino.pokmoniquii.ui.InfoDialog

open class AppCompatActivityV2 : AppCompatActivity(){
    companion object{
        lateinit var lastInstance: AppCompatActivityV2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lastInstance = this
        this.customizeTopBar()
    }

    override fun onResume() {
        super.onResume()
        lastInstance = this
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun customizeTopBar(){
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.topbar))
        supportActionBar?.setCustomView(R.layout.custom_top_bar)

        findViewById<View>(R.id.topbar_info).setOnClickListener {
            InfoDialog(this).show()
        }

        findViewById<View>(R.id.topbar_back).setOnClickListener {
            onBackPressed()
        }
    }


}