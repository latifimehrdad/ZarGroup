package com.zarholding.zar.model.response.trip

import com.zarholding.zar.model.enum.EnumTripStatus
import com.zarholding.zardriver.model.response.TripStationModel

/**
 * Created by m-latifi on 11/22/2022.
 */

data class TripModel(
    val id : Int,
    val commuteTripName : String?,
    val originName : String?,
    val destinationName : String?,
    val leaveTime : String?,
    val commuteDriverId : Int,
    val commuteDriverName : String?,
    val companyCode : String?,
    val companyName : String?,
    val tripPoints : List<TripPointModel>?,
    val stations : List<TripStationModel>?,
    val strTripPoint : String?,
    val myStationTripId : Int,
    val myStationTripStatus : EnumTripStatus?,
    val myStationName : String?,
    val carImageName : String?,
    val driverImageName : String?,
    val myStationTripRequestResone: String?,
    val myStationArriveTime : String?
)