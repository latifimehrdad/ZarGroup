package com.zarholding.zar.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.TimePicker
import com.google.android.material.button.MaterialButton
import com.zarholding.zar.view.SleepTimePicker
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import zar.R
import java.util.*

class TimeMultiDialog(
    context: Context,
    private val click: Click) : Dialog(context){

    lateinit var textViewWent : TextView
    lateinit var textViewForth : TextView


    //---------------------------------------------------------------------------------------------- Click
    interface Click {
        fun clickYes(timeWent : String, timeForth : String)
    }
    //---------------------------------------------------------------------------------------------- Click


    //---------------------------------------------------------------------------------------------- onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_time_multi)
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
        val timePicker = this.findViewById<SleepTimePicker>(R.id.timePicker)
        textViewWent = this.findViewById(R.id.textViewWent)
        textViewForth = this.findViewById(R.id.textViewForth)

        timePicker.setTime(LocalTime.of(17, 0), LocalTime.of(8, 0))
        handleUpdate(timePicker.getBedTime(), timePicker.getWakeTime())

        timePicker.listener = { bedTime: LocalTime, wakeTime: LocalTime ->
            handleUpdate(bedTime, wakeTime)
        }

        buttonConfirm.setOnClickListener {
/*            val hour = String.format("%02d", timePicker.hour)
            val minute = String.format("%02d", timePicker.minute)*/
            val formatter = DateTimeFormatter.ofPattern("hh:mm", Locale.US)
            click.clickYes(timePicker.getWakeTime().format(formatter),
                timePicker.getBedTime().format(formatter))
            dismiss()
        }

        buttonCancel.setOnClickListener {
            dismiss()
        }
    }
    //---------------------------------------------------------------------------------------------- initDialog


    private fun handleUpdate(bedTime: LocalTime, wakeTime: LocalTime) {
        val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
        textViewForth.text = bedTime.format(formatter)
        textViewWent.text = wakeTime.format(formatter)

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

}