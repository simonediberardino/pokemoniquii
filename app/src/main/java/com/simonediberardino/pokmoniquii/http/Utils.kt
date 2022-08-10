package com.simonediberardino.pokmoniquii.http

import android.graphics.BitmapFactory

import android.graphics.Bitmap
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


object Utils {
    @Throws(MalformedURLException::class, IOException::class)
    fun bitmapFromUrl(url: String): Bitmap? {
        val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
        connection.setRequestProperty("User-agent", "Mozilla/4.0")
        connection.connect()
        val input: InputStream = connection.inputStream
        return BitmapFactory.decodeStream(input)
    }
}