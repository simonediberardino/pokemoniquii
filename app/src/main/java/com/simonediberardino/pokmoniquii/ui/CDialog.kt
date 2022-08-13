package com.simonediberardino.pokmoniquii.ui

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.simonediberardino.pokmoniquii.R
import com.simonediberardino.pokmoniquii.utils.Utils

class CDialog(
    private var c: Activity,
    private var title: String,
    private val confirmCallback: Runnable = Runnable {},
    private var option1: String = c.getString(R.string.ok),
    private var option2: String = c.getString(R.string.cancel),
    private val cancelCallback: Runnable = Runnable {},
) :
    Dialog(c) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog)

        findViewById<TextView>(R.id.dialog_title).also {
            it.text = this.title
        }

        findViewById<Button>(R.id.dialog_confirm_btn).also {
            it.setOnClickListener {
                confirmCallback.run()
                dismiss()
            }

            it.text = option1
        }

        findViewById<TextView>(R.id.dialog_cancel_btn).also {
            it.setOnClickListener {
                dismiss()
            }

            it.text = option2
        }

        findViewById<View>(R.id.dialog_close).also {
            dismiss()
        }

        setOnDismissListener { cancelCallback.run() }
    }
}