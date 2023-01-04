package com.zarholding.zar.utility.extension

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.zar.core.tools.extensions.toSolarDate
import com.zarholding.zar.model.enum.EnumTripStatus
import com.zarholding.zar.model.notification_signalr.NotificationSignalrModel
import com.zarholding.zar.model.response.PassengerModel
import com.zarholding.zar.model.response.address.AddressModel
import com.zarholding.zardriver.model.response.TripStationModel
import zar.R
import java.time.Duration
import java.time.LocalDateTime

/**
 * Created by m-latifi on 11/14/2022.
 */


//-------------------------------------------------------------------------------------------------- getMessageContent
@BindingAdapter("getMessageContent")
fun TextView.getMessageContent(notification: NotificationSignalrModel?) {
    text = notification.getMessageContent()
}
//-------------------------------------------------------------------------------------------------- getMessageContent


//-------------------------------------------------------------------------------------------------- setElapseTime
@BindingAdapter("setElapseTime")
fun TextView.setElapseTime(dateTime: String?) {
    dateTime?.let {
        val now = LocalDateTime.now()
        val date = LocalDateTime.parse(dateTime)
        val minuteBetween = Duration.between(date, now).toMinutes()
        val hoursBetween = Duration.between(date, now).toHours()
        text = if (minuteBetween < 60)
            context.getString(R.string.minuteAgo, minuteBetween.toString())
        else if (hoursBetween < 24)
            context.getString(R.string.hoursAgo, hoursBetween.toString())
        else {
            val daysBetween = Duration.between(date, now).toDays()
            context.getString(R.string.dayAgo, daysBetween.toString())
        }


    }
}
//-------------------------------------------------------------------------------------------------- setElapseTime


//-------------------------------------------------------------------------------------------------- setTitleAndValueText
@BindingAdapter("setTitleText", "setValueText")
fun TextView.setTitleAndValueText(title: String?, value: String?) {
    text = context.getString(R.string.setTwoStringDot, title, value)
}
//-------------------------------------------------------------------------------------------------- setTitleAndValueText


//-------------------------------------------------------------------------------------------------- setTitleAndValueDash
@BindingAdapter("setOneValueToTextView", "setTwoValueToTextView")
fun TextView.setTitleAndValueDash(one: String?, two: String?) {
    text = context.getString(R.string.setTwoString, one, two)
}
//-------------------------------------------------------------------------------------------------- setTitleAndValueDash


//-------------------------------------------------------------------------------------------------- setCommuteTripAndDriverName
@BindingAdapter("setCommuteTripName", "setDriverName")
fun TextView.setCommuteTripAndDriverName(commuteTripName: String?, driverName: String?) {
    text = context.getString(R.string.setTwoString, commuteTripName, driverName)
}
//-------------------------------------------------------------------------------------------------- setCommuteTripAndDriverName


//-------------------------------------------------------------------------------------------------- setOriginDestinationToTextView
@BindingAdapter("setOriginToTextView", "setDestinationToTextView")
fun TextView.setOriginDestinationToTextView(origin: String?, destination: String?) {
    text = context.getString(R.string.setTwoStringSlash, origin, destination)
}
//-------------------------------------------------------------------------------------------------- setOriginDestinationToTextView


//-------------------------------------------------------------------------------------------------- setDepartureReturnToTextView
@BindingAdapter("setDepartureToTextView", "setReturnToTextView")
fun TextView.setDepartureReturnToTextView(departure: String?, _return: String?) {
    val temp = _return?.trimStart()
    text = if (temp.isNullOrEmpty())
        departure
    else
        context.getString(R.string.setTwoString, departure, _return)
}
//-------------------------------------------------------------------------------------------------- setDepartureReturnToTextView


//-------------------------------------------------------------------------------------------------- setPassengersToTextView
@BindingAdapter("setPassengersToTextView")
fun TextView.setPassengersToTextView(passengers: List<PassengerModel>?) {
    var title = context.getString(R.string.passengersDot)
    passengers?.let {
        for (i in passengers.indices)
            title += if (i == passengers.size - 1)
                passengers[i].value
            else
                "${passengers[i].value} - "
    }
    text = title
}
//-------------------------------------------------------------------------------------------------- setPassengersToTextView


//-------------------------------------------------------------------------------------------------- setWaitingTimeToTextView
@BindingAdapter("setWaitingTimeToTextView")
fun TextView.setWaitingTimeToTextView(time: Int) {
    text = if (time < 59)
        "$time دقیقه پیش "
    else {
        val h = time / 60
        "$h ساعت پیش "
    }
}
//-------------------------------------------------------------------------------------------------- setWaitingTimeToTextView


//-------------------------------------------------------------------------------------------------- setReasonToTextView
@BindingAdapter("setReasonToTextView")
fun TextView.setReasonToTextView(reason: String?) {
    val title = context.getString(R.string.reasonOfTripDot) + " " + reason
    text = title
}
//-------------------------------------------------------------------------------------------------- setReasonToTextView


//-------------------------------------------------------------------------------------------------- setApplicatorNameToTextView
@BindingAdapter("setApplicatorNameToTextView")
fun TextView.setApplicatorNameToTextView(name: String?) {
    text = context.getString(R.string.applicator, name)
}
//-------------------------------------------------------------------------------------------------- setApplicatorNameToTextView


//-------------------------------------------------------------------------------------------------- setAddressToTextview
@BindingAdapter("setAddressToTextview")
fun TextView.setAddressToTextview(address: AddressModel) {
    text = address.getAddress()
}
//-------------------------------------------------------------------------------------------------- setAddressToTextview


//-------------------------------------------------------------------------------------------------- setPersonnelNameCode
@BindingAdapter("setPersonnelName", "setPersonnelCode")
fun TextView.setPersonnelNameCode(name: String, code: String) {
    text = context.getString(R.string.setTwoString, name, code)
}
//-------------------------------------------------------------------------------------------------- setPersonnelNameCode


//-------------------------------------------------------------------------------------------------- setRequestReason
@BindingAdapter("setRequestReason")
fun TextView.setRequestReason(reason: String?) {
    text = context.getString(R.string.reasonOfReject1, reason)
    setTextColor(context.getColor(R.color.rejectGradiantEndColor))
}
//-------------------------------------------------------------------------------------------------- setRequestReason


//-------------------------------------------------------------------------------------------------- setRequester
@BindingAdapter("setRequester")
fun TextView.setRequester(userName: String) {
    text = context.getString(R.string.requester, userName)
}
//-------------------------------------------------------------------------------------------------- setRequester


//-------------------------------------------------------------------------------------------------- setRequester
@BindingAdapter("setDriverName", "setCommuteTripName", "setStationName")
fun TextView.setDriverAndStation(driverName: String, tripName: String, stationName: String) {
    text = context.getString(R.string.setThreeString, tripName, driverName, stationName)
}
//-------------------------------------------------------------------------------------------------- setRequester


//-------------------------------------------------------------------------------------------------- setRegisterStationStatus
fun TextView.setRegisterStationStatus(status: EnumTripStatus?) {
    status?.let {
        text = when (status) {
            EnumTripStatus.Pending -> context.resources.getString(R.string.pendingForAccept)
            EnumTripStatus.Confirmed -> context.resources.getString(R.string.confirmedByOfficial)
            EnumTripStatus.Reject -> context.resources.getString(R.string.reject)
        }
    }
}
//-------------------------------------------------------------------------------------------------- setRegisterStationStatus



//-------------------------------------------------------------------------------------------------- setMyStation
@BindingAdapter("setMyStation", "setArriveTime")
fun TextView.setMyStation(myStation: String?, arriveTime: String?) {
    val title = "${context.getString(R.string.myStation, myStation)} - $arriveTime"
    text = title
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




