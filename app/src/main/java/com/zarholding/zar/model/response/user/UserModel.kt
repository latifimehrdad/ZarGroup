package com.zarholding.zar.model.response.user

data class UserModel(
    val id : Int,
    val createDate : String?,
    val lastUpdate : String?,
    val userName : String?,
    val fullName : String?,
    val mobile : String?,
    val email : String?,
    val personnelNumber : String?,
    val phone : String?
)
