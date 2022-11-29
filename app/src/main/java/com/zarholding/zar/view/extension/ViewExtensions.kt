package com.zarholding.zar.view.extension

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.load
import com.zar.core.tools.extensions.toSolarDate
import com.zarholding.zar.hilt.Providers
import com.zarholding.zardriver.model.response.TripStationModel
import zar.R
import java.time.LocalDateTime

/**
 * Created by m-latifi on 11/14/2022.
 */


//-------------------------------------------------------------------------------------------------- setMyStation
@BindingAdapter("setMyStation")
fun TextView.setMyStation(myStation : String?) {
    myStation?.let {
        val title = "${context.getString(R.string.myStation)} : $it"
        text = title
    } ?: run {
        text = ""
    }
}
//-------------------------------------------------------------------------------------------------- setMyStation


//-------------------------------------------------------------------------------------------------- setStation
@BindingAdapter("setStation")
fun TextView.setStation(stations: List<TripStationModel>?) {
    var title = "${context.getString(R.string.stations)} : "
    stations?.let {
        for (i in it.indices)
            title += if (i + 1 < it.size)
                "${it[i].stationName} - "
            else
                it[i].stationName
    }
    text = title
}
//-------------------------------------------------------------------------------------------------- setStation


//-------------------------------------------------------------------------------------------------- setStartEndStation
@BindingAdapter("setOriginName", "setDestinationName")
fun TextView.setStartEndStation(originName: String?, destinationName: String?) {
    var title = ""
    originName?.let {
        title += "${context.getString(R.string.origin)} : $originName"
    }
    destinationName.let {
        title += " / ${context.getString(R.string.destination)} : $destinationName"
    }
    text = title
}
//-------------------------------------------------------------------------------------------------- setStartEndStation


//-------------------------------------------------------------------------------------------------- ImageView.setAppIcon
@BindingAdapter("setAppIcon")
fun ImageView.setAppIcon(icon: Int) {
    setImageResource(icon)
}
//-------------------------------------------------------------------------------------------------- ImageView.setAppIcon


//-------------------------------------------------------------------------------------------------- ImageView.setAppIcon
@BindingAdapter("setAppComingSoon")
fun View.setAppComingSoon(link: Int) {
    visibility = if (link == 0)
        View.VISIBLE
    else
        View.GONE
}
//-------------------------------------------------------------------------------------------------- ImageView.setAppIcon


//-------------------------------------------------------------------------------------------------- TextView.setDateTime
@BindingAdapter("setDateTime")
fun TextView.setDateTime(localDateTime: LocalDateTime?) {
    localDateTime?.let { date ->
        val solarDateModel = date.toSolarDate()
        solarDateModel?.let {
            text = it.getFullDate()
        } ?: run {
            text = ""
        }
    } ?: run {
        text = ""
    }
}
//-------------------------------------------------------------------------------------------------- TextView.setDateTime


//-------------------------------------------------------------------------------------------------- loadImage
@BindingAdapter("loadImage", "setEntityType")
fun ImageView.loadImage(url: String, entityType: String) {
    val circularProgressDrawable = CircularProgressDrawable(this.context)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()
    val link = "${Providers.url}/api/v1/Content/file?entityType=$entityType&fileName=$url"
    this.load(link) {
        crossfade(true)
        placeholder(circularProgressDrawable)
        allowHardware(false)
        bitmapConfig(Bitmap.Config.ARGB_8888)
    }
}
//-------------------------------------------------------------------------------------------------- loadImage


//-------------------------------------------------------------------------------------------------- loadImage1
@BindingAdapter("loadImage1")
fun ImageView.loadImage1(url: String) {
    val circularProgressDrawable = CircularProgressDrawable(this.context)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()

    this.load(url) {
        crossfade(true)
        placeholder(circularProgressDrawable)
        allowHardware(false)
        bitmapConfig(Bitmap.Config.ARGB_8888)
    }
}
//-------------------------------------------------------------------------------------------------- loadImage1


//-------------------------------------------------------------------------------------------------- setUnreadNotification
@BindingAdapter("setUnreadNotification")
fun View.setUnreadNotification(read: Boolean) {
    if (read)
        setBackgroundColor(Color.TRANSPARENT)
    else
        setBackgroundColor(
            context
                .resources
                .getColor(R.color.notificationUnreadColor, context.theme)
        )
}
//-------------------------------------------------------------------------------------------------- setUnreadNotification


//-------------------------------------------------------------------------------------------------- hideKeyboard
fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}
//-------------------------------------------------------------------------------------------------- hideKeyboard