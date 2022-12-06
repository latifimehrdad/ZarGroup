package com.zarholding.zar.model.response.user

import com.zarholding.zar.database.entity.RoleEntity
import com.zarholding.zar.model.response.BaseResponseAbstractModel

data class UserPermissionResponseModel (
        override val hasError: Boolean,
        override val message: String,
        val data : List<String>?
) : BaseResponseAbstractModel()