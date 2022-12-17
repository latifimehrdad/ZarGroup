package com.zarholding.zar.model.request

data class TaxiAddFavPlaceRequest(
    val locationName : String,
    val locationAddress : String,
    val lat : Double,
    val long : Double
)
