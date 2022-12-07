package com.zarholding.zar.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import android.widget.TimePicker
import com.google.android.material.button.MaterialButton
import zar.R

class TimeSingleDialog(
    context: Context,
    private val click: Click) : Dialog(context){


    //---------------------------------------------------------------------------------------------- Click
    interface Click {
        fun clickYes(time : String)
    }
    //---------------------------------------------------------------------------------------------- Click


    //---------------------------------------------------------------------------------------------- onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_time_single)
        val lp = WindowManager.LayoutParams()
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        this.window?.setGravity(Gravity.CENTER)
        lp.copyFrom(this.window?.attributes)
        lp.horizontalMargin = 50f
        this.window?.attributes = lp
    }
    //---------------------------------------------------------------------------------------------- onCreate


    //---------------------------------------------------------------------------------------------- onStart
    override fun onStart() {
        initDialog()
        super.onStart()
    }
    //---------------------------------------------------------------------------------------------- onStart



    //---------------------------------------------------------------------------------------------- initDialog
    private fun initDialog() {
        val buttonConfirm = this.findViewById<MaterialButton>(R.id.buttonConfirm)
        val buttonCancel = this.findViewById<MaterialButton>(R.id.buttonCancel)
        val timePicker = this.findViewById<TimePicker>(R.id.timePicker)

        buttonConfirm.setOnClickListener {
            val hour = String.format("%02d", timePicker.hour)
            val minute = String.format("%02d", timePicker.minute)
            click.clickYes("$hour:$minute")
            dismiss()
        }

        buttonCancel.setOnClickListener {
            dismiss()
        }
    }
    //---------------------------------------------------------------------------------------------- initDialog

}