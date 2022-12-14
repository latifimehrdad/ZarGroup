package com.zarholding.zar.view.extension

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.zar.core.tools.extensions.toSolarDate
import com.zarholding.zar.hilt.Providers
import com.zarholding.zar.model.enum.EnumTripStatus
import com.zarholding.zar.model.response.address.AddressModel
import com.zarholding.zardriver.model.response.TripStationModel
import zar.R
import java.time.LocalDateTime

/**
 * Created by m-latifi on 11/14/2022.
 */

//-------------------------------------------------------------------------------------------------- AddressModel.getAddress()
fun AddressModel.getAddress() : String {
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



//-------------------------------------------------------------------------------------------------- setAddressToTextview
@BindingAdapter("setAddressToTextview")
fun TextView.setAddressToTextview(address : AddressModel) {
    text = address.getAddress()
}
//-------------------------------------------------------------------------------------------------- setAddressToTextview



//-------------------------------------------------------------------------------------------------- setPersonnelNameCode
@BindingAdapter("setPersonnelName", "setPersonnelCode")
fun TextView.setPersonnelNameCode(name : String, code : String) {
    text = context.getString(R.string.setTwoString, name, code)
}
//-------------------------------------------------------------------------------------------------- setPersonnelNameCode


//-------------------------------------------------------------------------------------------------- setRequestReason
@BindingAdapter("setRequestReason")
fun TextView.setRequestReason(reason : String?) {
    text = context.getString(R.string.reasonOfReject1, reason)
    setTextColor(context.getColor(R.color.rejectGradiantEndColor))
}
//-------------------------------------------------------------------------------------------------- setRequestReason


//-------------------------------------------------------------------------------------------------- setRequester
@BindingAdapter("setRequester")
fun TextView.setRequester(userName : String) {
    text = context.getString(R.string.requester, userName)
}
//-------------------------------------------------------------------------------------------------- setRequester


//-------------------------------------------------------------------------------------------------- setRequester
@BindingAdapter("setDriverName", "setCommuteTripName" ,"setStationName")
fun TextView.setDriverAndStation(driverName : String, tripName : String , stationName : String) {
    text = context.getString(R.string.setThreeString, tripName, driverName, stationName)
}
//-------------------------------------------------------------------------------------------------- setRequester



//-------------------------------------------------------------------------------------------------- setRegisterStationStatus
fun CardView.setRegisterStationStatus(status : EnumTripStatus?){
    status?.let {
        when(status) {
            EnumTripStatus.IsPending -> setCardBackgroundColor(context.resources.getColor(R.color.waiting, context.theme))
            EnumTripStatus.Done -> setCardBackgroundColor(context.resources.getColor(R.color.positive, context.theme))
            EnumTripStatus.Reject -> setCardBackgroundColor(context.resources.getColor(R.color.negative, context.theme))
        }
    }
}
//-------------------------------------------------------------------------------------------------- setRegisterStationStatus



//-------------------------------------------------------------------------------------------------- setRegisterStationStatus
fun TextView.setRegisterStationStatus(status : EnumTripStatus?){
    status?.let {
        text = when(status) {
            EnumTripStatus.IsPending -> context.resources.getString(R.string.pendingForAccept)
            EnumTripStatus.Done -> context.resources.getString(R.string.confirmedByOfficial)
            EnumTripStatus.Reject -> context.resources.getString(R.string.reject)
        }
    }
}
//-------------------------------------------------------------------------------------------------- setRegisterStationStatus



//-------------------------------------------------------------------------------------------------- setRegisterStationStatus
fun ImageView.setRegisterStationStatus(status : EnumTripStatus?){
    status?.let {
        when(status) {
            EnumTripStatus.IsPending -> setImageResource(R.drawable.ic_pending)
            EnumTripStatus.Done -> setImageResource(R.drawable.ic_check)
            EnumTripStatus.Reject -> setImageResource(R.drawable.ic_delete)
        }
    }
}
//-------------------------------------------------------------------------------------------------- setRegisterStationStatus




//-------------------------------------------------------------------------------------------------- setMyStation
@BindingAdapter("setMyStation","setArriveTime")
fun TextView.setMyStation(myStation : String?, arriveTime : String?) {
    text = context.getString(R.string.myStation, myStation) +
            " \n " +
            context.getString(R.string.attendanceTime, arriveTime)
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
    Glide
        .with(this)
        .load(link)
        .into(this)
}
//-------------------------------------------------------------------------------------------------- loadImage



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

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}
//-------------------------------------------------------------------------------------------------- hideKeyboard