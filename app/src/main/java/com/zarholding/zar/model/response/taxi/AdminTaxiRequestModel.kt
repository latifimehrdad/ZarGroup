package com.zarholding.zar.model.response.taxi

import com.zarholding.zar.model.enum.EnumTaxiRequestType
import com.zarholding.zar.model.enum.EnumTaxiRequestStatus
import com.zarholding.zar.model.response.PassengerModel

data class AdminTaxiRequestModel(
    val id : Int,
    val userId : Int,
    val requesterName : String?,
    val status : EnumTaxiRequestStatus,
    val type : EnumTaxiRequestType,
    val departureDate : String?,
    val departureTime : String?,
    val returnDate : String?,
    val returnTime : String?,
    val originLat : Double,
    val originLong : Double,
    val originAddress : String?,
    val destinationLat : Double,
    val destinationLong : Double,
    val destinationAddress : String?,
    val passengers : List<Int>?,
    val listPassengers : List<PassengerModel>?,
    val strPassengers : String?,
    val travelReason : String?,
    val rejectReason : String?,
    val approverId : Int,
    val personnelJobKeyCode : String?,
    val companyCode : String?,
    val approverName : String?,
    val waitingTime : Int
)
