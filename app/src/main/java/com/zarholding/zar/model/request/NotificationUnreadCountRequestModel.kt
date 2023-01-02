package com.zarholding.zar.model.request

data class NotificationUnreadCountRequestModel(
    val receiverId : Int,
    val systemType : String,
    val lastId : Int
)