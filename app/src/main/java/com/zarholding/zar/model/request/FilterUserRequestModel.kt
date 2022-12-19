package com.zarholding.zar.model.request

data class FilterUserRequestModel(
    var PageNumber : Int,
    val PageSize : Int,
    var Search : String
)
