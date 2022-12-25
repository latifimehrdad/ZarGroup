package com.zarholding.zar.model.request

import com.zarholding.zar.model.enum.EnumTaxiRequestStatus

data class TaxiChangeStatusRequest(
    val Id : Int,
    val Status : EnumTaxiRequestStatus,
    val Reason : String?,
    val PersonnelJobKeyCode : String?,
    val fromCompany : String?
)
