package com.zarholding.zar.model.response.notification

import com.zarholding.zar.model.response.BaseResponseAbstractModel

data class NotificationUnreadResponse(
    override val hasError: Boolean,
    override val message: String,
    val data : NotificationUnreadCountModel?

) : BaseResponseAbstractModel()
