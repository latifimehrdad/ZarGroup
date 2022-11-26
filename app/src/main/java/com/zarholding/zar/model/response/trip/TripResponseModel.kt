package com.zarholding.zar.model.response.trip

import com.zarholding.zar.model.response.BaseResponseAbstractModel

/**
 * Created by m-latifi on 11/22/2022.
 */

data class TripResponseModel(
    override val hasError: Boolean,
    override val message: String,
    val data : TripModel?
    ) : BaseResponseAbstractModel()