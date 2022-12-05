package com.zarholding.zar.model.response.trip

import com.zarholding.zar.model.response.BaseResponseAbstractModel

data class TripRequestRegisterStatusResponseModel(
    override val hasError: Boolean,
    override val message: String,
    val data : Int

) : BaseResponseAbstractModel()
