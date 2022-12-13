package com.zarholding.zar.model.response.taxi

import com.zarholding.zar.model.response.BaseResponseAbstractModel

data class TaxiAddFavePlaceResponse(
    override val hasError: Boolean,
    override val message: String,
    val data: Boolean
) : BaseResponseAbstractModel()
