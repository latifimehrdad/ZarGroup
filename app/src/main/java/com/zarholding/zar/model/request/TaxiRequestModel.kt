package com.zarholding.zar.model.request

import com.zarholding.zar.model.enum.EnumTaxiRequest

data class TaxiRequestModel(
    val type : EnumTaxiRequest,
    val departureDate : String,
    val departureTime : String,
    val originLat : Double,
    val originLong : Double,
    val originAddress : String,
    val destinationLat : Double,
    val destinationLong : Double,
    val destinationAddress : String,
    val passengers : List<Int>,
    val travelReason : String
)
