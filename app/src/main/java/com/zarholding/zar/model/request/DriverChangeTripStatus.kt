package com.zarholding.zar.model.request

import com.zarholding.zar.model.enum.EnumTripStatus

/**
 * Create by Mehrdad on 1/7/2023
 */
data class DriverChangeTripStatus(
    val Id : Int,
    val TripStatus : EnumTripStatus,
    val TripLat : Double,
    val TripLong : Double
)
