package com.zarholding.zar.model.notification_signalr

data class NotificationSignalrModel(
    val lastUpdatePersian : String?,
    val receiverName : String?,
    val isRead : Boolean,
    val message : String?,
    val statusMessage : String?,
    val senderId : Int,
    val senderName : String?,
    val receiverId : Int,
    val messageType : String?,
    val lastUpdate : String?,
    val systemType : String,
    val destinationWebToken : String?,
    val id : Int,
    val status : String?
)