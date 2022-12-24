package com.zarholding.zar.repository

import com.zar.core.tools.api.apiCall
import com.zarholding.zar.api.ApiSuperApp
import com.zarholding.zar.model.request.LoginRequestModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/9/2022.
 */

class LoginRepository @Inject constructor(private val api: ApiSuperApp) {

    //---------------------------------------------------------------------------------------------- requestLogin
    suspend fun requestLogin(login : LoginRequestModel) =
        apiCall{ api.requestLogin(login) }
    //---------------------------------------------------------------------------------------------- requestLogin


}