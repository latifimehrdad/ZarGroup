package com.zarholding.zar.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.zarholding.zar.view.WentTimePicker
import zar.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class TimeDialog(
    context: Context,
    private val pickerMode: WentTimePicker.PickerMode,
    private val click: Click) : Dialog(context){

    private lateinit var textViewWent : TextView
    private lateinit var textViewForth : TextView
    private lateinit var timePicker : WentTimePicker

    //---------------------------------------------------------------------------------------------- Click
    interface Click {
        fun clickYes(timeWent : String, timeForth : String)
    }
    //---------------------------------------------------------------------------------------------- Click


    //---------------------------------------------------------------------------------------------- onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_time)
        val lp = WindowManager.LayoutParams()
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
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
        val linearLayoutWent = this.findViewById<LinearLayout>(R.id.linearLayoutWent)
        val linearLayoutForth = this.findViewById<LinearLayout>(R.id.linearLayoutForth)
        textViewWent = this.findViewById(R.id.textViewWent)
        textViewForth = this.findViewById(R.id.textViewForth)
        timePicker = findViewById(R.id.timePicker)
        when(pickerMode) {
            WentTimePicker.PickerMode.WENT -> {
                linearLayoutForth.visibility = View.GONE
                textViewForth.visibility = View.GONE
                linearLayoutWent.visibility = View.VISIBLE
                textViewWent.visibility = View.VISIBLE
            }
            WentTimePicker.PickerMode.FORTH -> {
                linearLayoutWent.visibility = View.GONE
                textViewWent.visibility = View.GONE
                linearLayoutForth.visibility = View.VISIBLE
                textViewForth.visibility = View.VISIBLE
            }
            WentTimePicker.PickerMode.WENT_FORTH -> {
                linearLayoutForth.visibility = View.VISIBLE
                textViewForth.visibility = View.VISIBLE
                linearLayoutWent.visibility = View.VISIBLE
                textViewWent.visibility = View.VISIBLE
            }
        }

        timePicker.setTime(
            LocalTime.of(8, 0),
            LocalTime.of(17, 0),
            pickerMode)

        handleUpdate(timePicker.getWentTime(), timePicker.getForthTime())

        timePicker.listener = { bedTime: LocalTime, wakeTime: LocalTime ->
            handleUpdate(bedTime, wakeTime)
        }

        buttonConfirm.setOnClickListener {
/*            val hour = String.format("%02d", timePicker.hour)
            val minute = String.format("%02d", timePicker.minute)*/
            val formatter = DateTimeFormatter.ofPattern("hh:mm", Locale.US)
            click.clickYes(timePicker.getWentTime().format(formatter),
                timePicker.getForthTime().format(formatter))
            this.cancel()
        }

        buttonCancel.setOnClickListener {
            this.cancel()
        }
    }
    //---------------------------------------------------------------------------------------------- initDialog



    //---------------------------------------------------------------------------------------------- handleUpdate
    private fun handleUpdate(wentTime: LocalTime, forthTime: LocalTime) {
        val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
        textViewForth.text = forthTime.format(formatter)
        textViewWent.text = wentTime.format(formatter)

/*        val bedDate = bedTime.atDate(LocalDate.now())
        var wakeDate = wakeTime.atDate(LocalDate.now())
        if (bedDate >= wakeDate) wakeDate = wakeDate.plusDays(1)
        val duration = Duration.between(bedDate, wakeDate)
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60
        tvHours.text = hours.toString()
        tvMins.text = minutes.toString()
        if (minutes > 0) llMins.visibility = View.VISIBLE else llMins.visibility = View.GONE*/
    }
    //---------------------------------------------------------------------------------------------- handleUpdate



    //---------------------------------------------------------------------------------------------- dismiss
    override fun dismiss() {
        super.dismiss()
        timePicker.setTime(
            LocalTime.of(8, 0),
            LocalTime.of(17, 0),
            WentTimePicker.PickerMode.WENT_FORTH)
    }
    //---------------------------------------------------------------------------------------------- dismiss



}