package com.zarholding.zar.model.response.driver

import com.zarholding.zar.model.response.BaseResponseAbstractModel

data class DriverResponse(
    override val hasError: Boolean,
    override val message: String,
    val data : List<DriverModel>?
) : BaseResponseAbstractModel()
