package com.zarholding.zar.model.request

import com.zarholding.zar.model.enum.EnumTaxiRequestType

data class TaxiRequestModel(
    val type : EnumTaxiRequestType,
    val departureDate : String,
    val departureTime : String,
    val returnDate : String?,
    val returnTime : String?,
    val originLat : Double,
    val originLong : Double,
    val originAddress : String,
    val destinationLat : Double,
    val destinationLong : Double,
    val destinationAddress : String,
    val passengers : List<Int>,
    val travelReason : String,
    val companyCode : String?,
    val personnelJobKeyCode : String?
)
