package com.zarholding.zar.model.response.address

data class AddressSuggestionModel(
    val place_id : Int,
    val lat : Double,
    val lon : Double,
    val boundingbox : List<Double>,
    val address : AddressModel
)
