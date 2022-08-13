package com.simonediberardino.pokmoniquii.utils

import android.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.util.DisplayMetrics
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.simonediberardino.pokmoniquii.data.CacheData.context
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


object Utils {
    /**
     * Returns wheter the user scrolled the scroll view till the bottom or not
     */
    fun ScrollView.isAtBottom(): Boolean {
        val view = getChildAt(childCount - 1)
        return view.bottom - (height + scrollY) == 0
    }

    /**
     * Downloads and returns a bitmap from a given URL
     */
    @Throws(MalformedURLException::class, IOException::class)
    fun bitmapFromUrl(url: String, activity: Activity): Bitmap? {
        if(!isInternetAvailable(activity))
            return null

        val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
        connection.setRequestProperty("User-agent", "Mozilla/4.0")
        connection.connect()
        val input: InputStream = connection.inputStream
        return BitmapFactory.decodeStream(input)
    }

    fun isInternetAvailable(appCompatActivity: Activity): Boolean {
        val cm = appCompatActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nInfo = cm.activeNetworkInfo
        return nInfo != null && nInfo.isAvailable && nInfo.isConnected
    }

    fun navigateTo(c: AppCompatActivity, cl: Class<*>?, animation: Boolean = true){
        val intent = Intent(c, cl)

        if(animation){
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }else{
            c.overridePendingTransition(0, 0)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        }

        val bundle = ActivityOptionsCompat.makeCustomAnimation(c, R.anim.fade_in, R.anim.fade_out).toBundle()
        c.startActivity(intent, bundle)
    }

    fun getScreenHeight(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    fun getScreenWidth(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    fun String.capitalizeWords(): String =
        lowercase().split(" ").joinToString(" ") { it.capitalize() }
}

