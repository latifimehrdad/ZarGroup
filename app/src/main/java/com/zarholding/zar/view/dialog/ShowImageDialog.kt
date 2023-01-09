package com.zarholding.zar.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import com.zarholding.zar.model.other.ShowImageModel
import com.zarholding.zar.utility.TouchImageView
import com.zarholding.zar.view.extension.loadImage
import com.zarholding.zar.view.extension.loadImageByToken
import zar.R

class ShowImageDialog(
    context: Context,
    private val item : ShowImageModel) : Dialog(context) {


    //---------------------------------------------------------------------------------------------- onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_show_image)
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
        val imageViewClose = this.findViewById<ImageView>(R.id.imageViewClose)
        val touchImageView = this.findViewById<TouchImageView>(R.id.touchImageView)
        item.token?.let {
            touchImageView.loadImageByToken(item.imageName, it)
        } ?: run {
            item.entityType?.let {
                touchImageView.loadImage(item.imageName, it)
            }
        }
        imageViewClose.setOnClickListener {
            dismiss()
        }
    }
    //---------------------------------------------------------------------------------------------- initDialog

}