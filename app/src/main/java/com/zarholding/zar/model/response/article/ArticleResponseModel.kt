package com.zarholding.zar.model.response.article

import com.zarholding.zar.model.response.BaseResponseAbstractModel

data class ArticleResponseModel(
    override val hasError: Boolean,
    override val message: String,
    val data : ArticleItemsModel?
) : BaseResponseAbstractModel()
