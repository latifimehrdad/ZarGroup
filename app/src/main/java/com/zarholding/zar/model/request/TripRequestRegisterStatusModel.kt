package com.zarholding.zar.model.request

import com.zarholding.zar.model.enum.EnumStatus

data class TripRequestRegisterStatusModel(
    val id: Int,
    val Status: EnumStatus,
    val Reason: String?
)