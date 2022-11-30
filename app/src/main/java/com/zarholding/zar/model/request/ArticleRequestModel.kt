package com.zarholding.zar.model.request

data class ArticleRequestModel(
    val PageNumber : Int,
    val PageSize : Int,
    val Search : String?,
    val isAdmin : Boolean,
    val ArticleType : String
)
