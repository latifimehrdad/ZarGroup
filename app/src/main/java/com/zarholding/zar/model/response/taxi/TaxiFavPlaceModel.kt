package com.zarholding.zar.model.response.taxi

import java.time.LocalDateTime

data class TaxiFavPlaceModel(
    val locationName : String?,
    val lat : Double,
    val long : Double,
    val userId : Int,
    val isAll : Boolean,
    val id : Int,
    val isActive : Boolean,
    val createDate : LocalDateTime?,
    val lastUpdate : LocalDateTime?,
    val createById : Int,
    val updateById : Int
)
