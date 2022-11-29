package com.zarholding.zar.model.response.trip

import com.zarholding.zar.model.response.BaseResponseAbstractModel

/**
 * Created by m-latifi on 11/9/2022.
 */

data class DeleteRegisteredStationResponseModel(
    override val hasError: Boolean,
    override val message: String,
    val data: Int
) : BaseResponseAbstractModel()
