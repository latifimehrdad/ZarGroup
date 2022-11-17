package com.zarholding.zar.view.extension

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.load
import com.zar.core.tools.extensions.toSolarDate
import java.time.LocalDateTime

/**
 * Created by m-latifi on 11/14/2022.
 */

//-------------------------------------------------------------------------------------------------- ImageView.setAppIcon
@BindingAdapter("setAppIcon")
fun ImageView.setAppIcon(icon : Int) {
    setImageResource(icon)
}
//-------------------------------------------------------------------------------------------------- ImageView.setAppIcon


//-------------------------------------------------------------------------------------------------- ImageView.setAppIcon
@BindingAdapter("setAppComingSoon")
fun View.setAppComingSoon(link : Int) {
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
@BindingAdapter("loadImage")
fun ImageView.loadImage(url : String) {
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
//-------------------------------------------------------------------------------------------------- loadImage



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