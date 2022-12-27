package com.zarholding.zar.model.request

import com.zarholding.zar.model.enum.EnumPersonnelType

data class AdminTaxiListRequest(
    var PageNumber : Int,
    val PageSize : Int,
    var UserType : EnumPersonnelType?
)
