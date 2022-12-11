package com.zarholding.zar.model.response.user

import com.zarholding.zar.database.entity.UserInfoEntity

data class UserItemsModel(
    val items : List<UserInfoEntity>?
)