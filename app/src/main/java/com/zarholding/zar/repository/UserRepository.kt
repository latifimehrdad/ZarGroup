package com.zarholding.zar.repository

import com.zar.core.tools.api.apiCall
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zar.api.ApiBPMS
import javax.inject.Inject

/**
 * Created by m-latifi on 11/26/2022.
 */

class UserRepository @Inject constructor(private val api: ApiBPMS) {

    @Inject
    lateinit var emitter: RemoteErrorEmitter

    //---------------------------------------------------------------------------------------------- requestUserInfo
    fun requestUserInfo(token: String) = apiCall(emitter) { api.requestUserInfo(token) }
    //---------------------------------------------------------------------------------------------- requestUserInfo


    //---------------------------------------------------------------------------------------------- requestUserPermission
    fun requestUserPermission(token: String) = apiCall(emitter) {api.requestUserPermission(token)}
    //---------------------------------------------------------------------------------------------- requestUserPermission


    //---------------------------------------------------------------------------------------------- requestGetUser
    fun requestGetUser(
        filters : String,
        value : String,
        token: String) = apiCall(emitter) {api.requestGetUser("end", filters, value, token)}
    //---------------------------------------------------------------------------------------------- requestGetUser

}