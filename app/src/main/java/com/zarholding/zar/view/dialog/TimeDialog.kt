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
import com.zar.core.view.picker.time.ZarTimePicker
import zar.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class TimeDialog(
    context: Context,
    private val pickerMode: ZarTimePicker.PickerMode,
    private val click: Click) : Dialog(context){

    private lateinit var textViewDeparture : TextView
    private lateinit var textViewReturn : TextView
    private lateinit var timePicker : ZarTimePicker

    //---------------------------------------------------------------------------------------------- Click
    interface Click {
        fun clickYes(timeDeparture : String, timeReturn : String)
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
        val linearLayoutDeparture = this.findViewById<LinearLayout>(R.id.linearLayoutDeparture)
        val linearLayoutReturn = this.findViewById<LinearLayout>(R.id.linearLayoutReturn)
        textViewDeparture = this.findViewById(R.id.textViewDeparture)
        textViewReturn = this.findViewById(R.id.textViewReturn)
        timePicker = findViewById(R.id.timePicker)
        when(pickerMode) {
            ZarTimePicker.PickerMode.DEPARTURE -> {
                linearLayoutReturn.visibility = View.GONE
                textViewReturn.visibility = View.GONE
                linearLayoutDeparture.visibility = View.VISIBLE
                textViewDeparture.visibility = View.VISIBLE
            }
            ZarTimePicker.PickerMode.RETURN -> {
                linearLayoutDeparture.visibility = View.GONE
                textViewDeparture.visibility = View.GONE
                linearLayoutReturn.visibility = View.VISIBLE
                textViewReturn.visibility = View.VISIBLE
            }
            ZarTimePicker.PickerMode.RETURNING -> {
                linearLayoutReturn.visibility = View.VISIBLE
                textViewReturn.visibility = View.VISIBLE
                linearLayoutDeparture.visibility = View.VISIBLE
                textViewDeparture.visibility = View.VISIBLE
            }
        }

        timePicker.setTime(
            LocalTime.of(8, 0),
            LocalTime.of(17, 0),
            pickerMode)

        handleUpdate(timePicker.getDepartureTime(), timePicker.getReturnTime())

        timePicker.listener = { bedTime: LocalTime, wakeTime: LocalTime ->
            handleUpdate(bedTime, wakeTime)
        }

        buttonConfirm.setOnClickListener {
            val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
            click.clickYes(timePicker.getDepartureTime().format(formatter),
                timePicker.getReturnTime().format(formatter))
            this.cancel()
        }

        buttonCancel.setOnClickListener {
            this.cancel()
        }
    }
    //---------------------------------------------------------------------------------------------- initDialog



    //---------------------------------------------------------------------------------------------- handleUpdate
    private fun handleUpdate(departureTime: LocalTime, returnTime: LocalTime) {
        val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
        textViewReturn.text = returnTime.format(formatter)
        textViewDeparture.text = departureTime.format(formatter)

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
            ZarTimePicker.PickerMode.RETURNING)
    }
    //---------------------------------------------------------------------------------------------- dismiss



}