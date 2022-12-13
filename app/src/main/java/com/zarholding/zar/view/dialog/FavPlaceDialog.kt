package com.zarholding.zar.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.EditText
import com.google.android.material.button.MaterialButton
import zar.R

class FavPlaceDialog(
    context: Context,
    private val click: Click) : Dialog(context){


    //---------------------------------------------------------------------------------------------- Click
    interface Click {
        fun clickSend(placeName : String)
    }
    //---------------------------------------------------------------------------------------------- Click


    //---------------------------------------------------------------------------------------------- onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_fav_place)
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
        val editTextName = this.findViewById<EditText>(R.id.editTextName)
        val buttonSend = this.findViewById<MaterialButton>(R.id.buttonSend)
        val buttonCancel = this.findViewById<MaterialButton>(R.id.buttonCancel)

        buttonSend.setOnClickListener {
            if (editTextName.text.isNullOrEmpty()) {
                editTextName.error = context.getString(R.string.pleaseEnterName)
            } else {
                click.clickSend(editTextName.text.toString())
                dismiss()
            }
        }

        buttonCancel.setOnClickListener {
            dismiss()
        }
    }
    //---------------------------------------------------------------------------------------------- initDialog

}