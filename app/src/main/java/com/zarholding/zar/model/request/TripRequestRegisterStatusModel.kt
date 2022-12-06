package com.zarholding.zar.model.request

import com.zarholding.zar.model.enum.EnumTripStatus

data class TripRequestRegisterStatusModel(
    val id: Int,
    val Status: EnumTripStatus,
    val Reason: String?
)