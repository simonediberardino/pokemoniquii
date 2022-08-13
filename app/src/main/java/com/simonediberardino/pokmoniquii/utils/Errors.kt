package com.simonediberardino.pokmoniquii.utils

import android.content.Context
import android.widget.Toast
import com.simonediberardino.pokmoniquii.R

class Error(val context: Context) {
    enum class CODE {
        NO_INTERNET
    }

    private val map: HashMap<CODE, String> =
        hashMapOf(CODE.NO_INTERNET to context.getString(R.string.error_no_internet))

    fun show(code: CODE) {
        Toast.makeText(context, map[code], Toast.LENGTH_LONG).show()
    }
}