package com.zarholding.zar.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import com.google.android.material.button.MaterialButton
import zar.R

class RejectReasonDialog(
    context: Context,
    private val click: Click) : Dialog(context){


    //---------------------------------------------------------------------------------------------- Click
    interface Click {
        fun clickSend(reason : String)
    }
    //---------------------------------------------------------------------------------------------- Click


    //---------------------------------------------------------------------------------------------- onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_reject_reason)
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
        val editTextReason = this.findViewById<EditText>(R.id.editTextReason)
        val buttonSend = this.findViewById<MaterialButton>(R.id.buttonSend)
        val buttonCancel = this.findViewById<MaterialButton>(R.id.buttonCancel)
        val imageViewClose = this.findViewById<ImageView>(R.id.imageViewClose)

        buttonSend.setOnClickListener {
            if (editTextReason.text.isNullOrEmpty()) {
                editTextReason.error = context.getString(R.string.pleaseEnterTheReason)
            } else {
                click.clickSend(editTextReason.text.toString())
                dismiss()
            }
        }

        buttonCancel.setOnClickListener {
            dismiss()
        }

        imageViewClose.setOnClickListener {
            dismiss()
        }
    }
    //---------------------------------------------------------------------------------------------- initDialog

}