package com.zarholding.zar.model.notification

data class NotificationMessageSignalrModel(
    val UserAction: String?,
    val ActionParam: String?,
    val Body: String?
)