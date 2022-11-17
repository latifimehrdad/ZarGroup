package com.zarholding.zar.model.other.notification

import java.time.LocalDateTime

/**
 * Created by m-latifi on 11/16/2022.
 */

data class NotificationModel(
    val icon : String,
    val title : String,
    val smallContent : String,
    val content : String,
    var dateTime : LocalDateTime,
    val read : Boolean
    )