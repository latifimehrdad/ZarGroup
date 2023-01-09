package com.zarholding.zar.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.zar.core.tools.extensions.toSolarDate
import com.zarholding.zar.model.notification_signalr.NotificationSignalrModel
import com.zarholding.zar.view.extension.getMessageContent
import zar.R
import java.time.LocalDateTime

class NotificationDetailDialog(
    context: Context,
    private val item : NotificationSignalrModel
    ) : Dialog(context) {


    //---------------------------------------------------------------------------------------------- onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_detail_notification)
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
        val textViewDate = this.findViewById<TextView>(R.id.textViewDate)
        val textViewSender = this.findViewById<TextView>(R.id.textViewSender)
        val textViewSummary = this.findViewById<TextView>(R.id.textViewSummary)
        val imageViewClose = this.findViewById<ImageView>(R.id.imageViewClose)

        val date = LocalDateTime.parse(item.lastUpdate)
        textViewDate.text = date.toSolarDate()?.getFullDate()
        textViewSender.text = item.senderName
        textViewSummary.text = item.getMessageContent()

        imageViewClose.setOnClickListener { dismiss() }

    }
    //---------------------------------------------------------------------------------------------- initDialog


}