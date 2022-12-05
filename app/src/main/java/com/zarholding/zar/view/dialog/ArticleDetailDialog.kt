package com.zarholding.zar.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.zarholding.zar.database.entity.ArticleEntity
import zar.R

class ArticleDetailDialog(
    context: Context,
    private val item : ArticleEntity
    ) : Dialog(context) {


    //---------------------------------------------------------------------------------------------- onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_detail_news)
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
        val textViewTitle = this.findViewById<TextView>(R.id.textViewTitle)
        val textViewSummary = this.findViewById<TextView>(R.id.textViewSummary)
        val textViewContent = this.findViewById<TextView>(R.id.textViewContent)
        val imageViewClose = this.findViewById<ImageView>(R.id.imageViewClose)


        textViewTitle.text = item.title
        textViewSummary.text = item.summary
        textViewContent.text = item.body

        imageViewClose.setOnClickListener { dismiss() }

    }
    //---------------------------------------------------------------------------------------------- initDialog


}