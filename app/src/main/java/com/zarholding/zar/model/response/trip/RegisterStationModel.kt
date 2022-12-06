package com.zarholding.zar.model.response.trip

import com.zarholding.zar.model.enum.EnumTripStatus
import java.time.LocalDateTime

/**
 * Created by m-latifi on 11/27/2022.
 */

data class RegisterStationModel(
    val userId : Int,
    val commuteTripId : Int,
    val stationTripId : Int,
    val status : EnumTripStatus?,
    val reason : String?,
    val id : Int,
    val isActive : Boolean,
    val createDate : LocalDateTime,
    val lastUpdate : LocalDateTime,
    val createById : Int,
    val updateById : Int
)
