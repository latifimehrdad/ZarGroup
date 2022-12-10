package com.zarholding.zar.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import zar.R

class ConfirmDialog(
    context: Context,
    private val type: ConfirmType,
    private val title : String,
    private val click: Click) : Dialog(context){


    //---------------------------------------------------------------------------------------------- ConfirmType
    enum class ConfirmType {
        ADD,
        DELETE
    }
    //---------------------------------------------------------------------------------------------- ConfirmType


    //---------------------------------------------------------------------------------------------- Click
    interface Click {
        fun clickYes()
    }
    //---------------------------------------------------------------------------------------------- Click


    //---------------------------------------------------------------------------------------------- onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_confirm)
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
        val layoutHeader = this.findViewById<ConstraintLayout>(R.id.constraintLayoutHeader)
        val textViewTitle = this.findViewById<TextView>(R.id.textViewTitle)
        val buttonYes = this.findViewById<MaterialButton>(R.id.buttonYes)
        val buttonNo = this.findViewById<MaterialButton>(R.id.buttonNo)

        when(type) {
            ConfirmType.ADD -> layoutHeader.setBackgroundResource(R.drawable.drawable_confirm)
            ConfirmType.DELETE -> layoutHeader.setBackgroundResource(R.drawable.drawable_reject)
        }

        textViewTitle.text = title

        buttonYes.setOnClickListener {
            click.clickYes()
            dismiss()
        }

        buttonNo.setOnClickListener {
            dismiss()
        }
    }
    //---------------------------------------------------------------------------------------------- initDialog

}