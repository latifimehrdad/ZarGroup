package com.zarholding.zar.model.response.company

import com.zarholding.zar.model.response.BaseResponseAbstractModel

data class CompanyResponse(
    override val hasError: Boolean,
    override val message: String,
    val data : CompanyItemsModel?

) : BaseResponseAbstractModel()
