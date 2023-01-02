package com.zarholding.zar.model.notification_signalr

import java.time.LocalDateTime

/**
 * Created by m-latifi on 11/16/2022.
 */

class NotificationCategoryModel(
    val name: String,
    val date : LocalDateTime?,
    val notifications: List<NotificationSignalrModel>
)