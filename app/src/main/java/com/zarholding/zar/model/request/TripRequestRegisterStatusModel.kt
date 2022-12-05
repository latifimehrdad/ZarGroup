package com.zarholding.zar.model.request

data class TripRequestRegisterStatusModel(
    val id: Int,
    val Status: Int,
    val Reason: String?
)