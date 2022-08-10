package com.simonediberardino.pokmoniquii.http

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory

import android.graphics.Bitmap
import android.net.ConnectivityManager
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


object Utils {
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
}

