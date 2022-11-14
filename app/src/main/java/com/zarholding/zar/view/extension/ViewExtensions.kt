package com.zarholding.zar.view.extension

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter

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