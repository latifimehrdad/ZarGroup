package com.zarholding.zar.model.response.trip

data class TripRequestRegisterModel(
    val id : Int,
    val userId : Int,
    val userName : String?,
    val stationName : String?,
    val stationNum : Int,
    val commuteTripName : String?,
    val originName : String?,
    val destinationName : String?,
    val driverName : String?,
    val companyName : String?,
    val companyCode : String?,
    var choose : Boolean = false
)
