package com.zarholding.zar.model.response.address

data class AddressModel(
    val neighbourhood : String?,
    val suburb : String?,
    val city : String?,
    val state_district : String?,
    val district : String?,
    val county : String?,
    val state : String?,
    val country : String?,
    val road : String?,
    val building : String?,
    val town : String?
)
