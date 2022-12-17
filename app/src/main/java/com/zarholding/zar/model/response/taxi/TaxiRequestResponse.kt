package com.zarholding.zar.model.response.taxi

import com.zarholding.zar.model.response.BaseResponseAbstractModel

data class TaxiRequestResponse(
    override val hasError: Boolean,
    override val message: String,
    val data: Int
) : BaseResponseAbstractModel()
