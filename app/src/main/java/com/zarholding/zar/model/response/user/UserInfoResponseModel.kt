package com.zarholding.zar.model.response.user

import com.zarholding.zar.database.entity.UserInfoEntity
import com.zarholding.zar.model.response.BaseResponseAbstractModel

/**
 * Created by m-latifi on 11/26/2022.
 */

data class UserInfoResponseModel(
    override val hasError: Boolean,
    override val message: String,
    val data : UserInfoEntity?
) : BaseResponseAbstractModel()
