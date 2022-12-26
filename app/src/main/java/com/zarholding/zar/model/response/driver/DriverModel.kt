package com.zarholding.zar.model.response.driver

import com.zarholding.zar.model.enum.EnumDriverType

data class DriverModel(
    val id : Int,
    val fullName : String?,
    val mobile : String?,
    val nationalCode : String?,
    val pelak : String?,
    val description : String?,
    val carImage : String?,
    val carImageName : String?,
    val driverImage : String?,
    val driverImageName : String?,
    val driverType : EnumDriverType,
    val companyCode : String?,
    val companyName : String?
)
