package com.zarholding.zar.model.response.trip

import com.zarholding.zar.model.response.BaseResponseAbstractModel

/**
 * Created by m-latifi on 11/27/2022.
 */

data class RegisterStationResponseModel(
    override val hasError: Boolean,
    override val message: String,
    val data : RegisterStationModel?
) : BaseResponseAbstractModel()
