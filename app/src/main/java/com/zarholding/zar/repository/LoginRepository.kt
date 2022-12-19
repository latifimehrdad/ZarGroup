package com.zarholding.zar.repository

import com.zar.core.tools.api.apiCall
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zar.api.ApiSuperApp
import com.zarholding.zar.model.request.LoginRequestModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/9/2022.
 */

class LoginRepository @Inject constructor(private val api: ApiSuperApp) {

    @Inject lateinit var emitter: RemoteErrorEmitter

    //---------------------------------------------------------------------------------------------- requestLogin
    fun requestLogin(login : LoginRequestModel) = apiCall(emitter){api.requestLogin(login)}
    //---------------------------------------------------------------------------------------------- requestLogin


}