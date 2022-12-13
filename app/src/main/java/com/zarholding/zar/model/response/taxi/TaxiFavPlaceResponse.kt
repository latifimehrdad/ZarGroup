package com.zarholding.zar.model.response.taxi

import com.zarholding.zar.model.response.BaseResponseAbstractModel

data class TaxiFavPlaceResponse(
    override val hasError: Boolean,
    override val message: String,
    val data: List<TaxiFavPlaceModel>?
) : BaseResponseAbstractModel()
