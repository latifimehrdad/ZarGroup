package com.zarholding.zar.api

import com.zarholding.zar.model.request.LoginRequestModel
import com.zarholding.zar.model.response.LoginResponseModel
import com.zarholding.zar.model.response.user.UserInfoResponseModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Created by m-latifi on 11/8/2022.
 */

interface ApiInterface {

    companion object {
        const val api = "/Api"
        const val v1 = "$api/V1"
        const val account = "$v1/Account"
        const val user = "$v1/User"
    }

    @POST("$account/login")
    suspend fun requestLogin(@Body login : LoginRequestModel) : LoginResponseModel


    @GET("$user/user-info")
    suspend fun requestUserInfo(
        @Header("Authorization") token : String
    ) : UserInfoResponseModel

}