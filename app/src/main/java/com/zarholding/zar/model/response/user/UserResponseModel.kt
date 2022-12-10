package com.zarholding.zar.model.response.user

import com.zarholding.zar.model.response.BaseResponseAbstractModel

data class UserResponseModel (
    override val hasError: Boolean,
    override val message: String,
    val data : UserItemsModel?

) : BaseResponseAbstractModel()