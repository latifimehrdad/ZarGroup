package com.zarholding.zar.model.response

import java.util.*

/**
 * Created by m-latifi on 11/9/2022.
 */

data class LoginResponseModel(
    override val hasError: Boolean,
    override val message: String,
    val data: String
) : BaseResponseAbstractModel()
