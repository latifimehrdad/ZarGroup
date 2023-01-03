package com.zarholding.zar.model.notification

import com.zarholding.zar.model.notification_signalr.NotificationSignalrModel
import com.zarholding.zar.model.response.BaseResponseAbstractModel

data class NotificationResponse(
    override val hasError: Boolean,
    override val message: String,
    val data : List<NotificationSignalrModel>
) : BaseResponseAbstractModel()