package com.zarholding.zar.view.extension

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.zarholding.zar.model.notification.NotificationMessageSignalrModel
import com.zarholding.zar.model.notification_signalr.NotificationSignalrModel
import com.zarholding.zar.model.response.address.AddressModel
import zar.R

/**
 * Created by m-latifi on 01/04/2023.
 */

//-------------------------------------------------------------------------------------------------- hideKeyboard
fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}
//-------------------------------------------------------------------------------------------------- hideKeyboard


//-------------------------------------------------------------------------------------------------- AddressModel.getAddress()
fun AddressModel.getAddress(): String {
    var addressText: String

    var county = county
    county = county?.replace("شهرستان", "")
    county = county?.replace("شهر", "")
    county = county?.trimStart()
    county = county?.trimEnd()
    if (county == null)
        county = ""

    var city = city
    city = city?.replace("شهرستان", "")
    city = city?.replace("شهر", "")
    city = city?.trimStart()
    city = city?.trimEnd()
    if (city == null)
        city = ""

    var town = town
    town = town?.replace("شهرستان", "")
    town = town?.replace("شهر", "")
    town = town?.trimStart()
    town = town?.trimEnd()
    if (town == null)
        town = ""

    addressText = if (city in county)
        county
    else if (county in city)
        city
    else
        "$county , $city"

    if (town !in addressText)
        addressText += " , $town"


    neighbourhood?.let {
        addressText += " , $it"
    }

    residential?.let {
        addressText += " , $it"
    }

    road?.let {
        addressText += " , $it"
    }

    return addressText
}
//-------------------------------------------------------------------------------------------------- AddressModel.getAddress()


//-------------------------------------------------------------------------------------------------- getMessageContent
fun NotificationSignalrModel?.getMessageContent() = this?.let {
    if (it.message.isNullOrEmpty())
        ""
    else {
        val gson = Gson()
        val messageContent = gson
            .fromJson(it.message, NotificationMessageSignalrModel::class.java)
        messageContent.Body
    }
}
//-------------------------------------------------------------------------------------------------- getMessageContent


//-------------------------------------------------------------------------------------------------- ImageView.setAppIcon
@BindingAdapter("setAppComingSoon")
fun View.setAppComingSoon(link: Int) {
    visibility = if (link == 0)
        View.VISIBLE
    else
        View.GONE
}
//-------------------------------------------------------------------------------------------------- ImageView.setAppIcon


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


//-------------------------------------------------------------------------------------------------- toCarPlaque
fun String?.toCarPlaque(tag: String) =
    if (this.isNullOrEmpty())
        ""
    else
        when (tag) {
            "number1" -> this.substring(0, 2)
            "alphabet" -> this.substring(2, 3)
            "number2" -> this.substring(3, 6)
            "city" -> this.substring(6, 8)
            else -> ""
        }
//-------------------------------------------------------------------------------------------------- toCarPlaque