package com.zarholding.zar.api

import com.zarholding.zar.model.request.LoginRequestModel
import com.zarholding.zar.model.response.LoginResponseModel
import com.zarholding.zar.model.response.user.UserInfoResponseModel
import com.zarholding.zar.model.response.user.UserPermissionResponseModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Created by m-latifi on 11/8/2022.
 */

interface ApiBPMS {

    companion object {
        const val api = "/Api"
        const val v1 = "$api/V1"
    }

    @POST("$v1/Account/login")
    suspend fun requestLogin(@Body login : LoginRequestModel) : LoginResponseModel


    @GET("$v1/User/user-info")
    suspend fun requestUserInfo(
        @Header("Authorization") token : String
    ) : UserInfoResponseModel

    @GET("$v1/Permission/user-permission")
    suspend fun requestUserPermission(
        @Header("Authorization") token : String
    ) : UserPermissionResponseModel

}