package com.zarholding.zar.repository

import com.zar.core.tools.api.apiCall
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zar.api.ApiSuperApp
import com.zarholding.zar.model.response.trip.RegisterStationModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/27/2022.
 */

class RegisterStationRepository @Inject constructor(private val api : ApiSuperApp) {

    @Inject lateinit var emitter: RemoteErrorEmitter

    //---------------------------------------------------------------------------------------------- requestRegisterStation
    fun requestRegisterStation(registerStationModel: RegisterStationModel, token : String) =
        apiCall(emitter){api.requestRegisterStation(registerStationModel, token)}
    //---------------------------------------------------------------------------------------------- requestRegisterStation

}