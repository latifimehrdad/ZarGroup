package com.zarholding.zar.model.other.notification

import java.time.LocalDateTime

/**
 * Created by m-latifi on 11/16/2022.
 */

class NotificationCategoryModel(
    val name: String,
    val date : LocalDateTime?,
    vararg notificationModel: NotificationModel
) {
    val listOfNotificationModel: List<NotificationModel> = notificationModel.toList()
}